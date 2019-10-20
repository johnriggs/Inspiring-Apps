package com.johnriggsdev.inspiringapps.ui.main

import android.annotation.SuppressLint
import com.johnriggsdev.inspiringapps.api.ApiServiceInterface
import com.johnriggsdev.inspiringapps.app.IAApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import android.widget.Toast
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.APP_TAG
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.FILE_NAME
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.TASK_DOWNLOAD
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.TASK_READ
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.TASK_SORT
import java.io.*
import retrofit2.Call
import retrofit2.Response


class MainRepository : MainContract.Repository{

    private lateinit var logCallback: LogDataCallback
    private lateinit var downloadZipFileTask: DownloadZipFileTask
    private var totalSize: Int = 1
    private var nLogN = 1
    private var stepCount = 0

    override fun setNetworkCallback(callback: LogDataCallback) {
        this.logCallback = callback
    }

    override fun fetchLogFileFromApi() {
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val call = ApiServiceInterface.create().getLogFromApi()
                call.enqueue(object : retrofit2.Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            downloadZipFileTask = DownloadZipFileTask()
                            downloadZipFileTask.execute(response.body())

                        } else {
                            logCallback.onApiError(response.message())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                        Log.e(APP_TAG, t.message!!)
                        logCallback.onApiError(t.localizedMessage!!)
                    }
                })
            } catch (e: Throwable) {
                CoroutineScope(Dispatchers.Main).launch {
                    logCallback.onApiError("API Error: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun onFileDownloaded(){
        CoroutineScope(Dispatchers.Default).launch {
            readAndSortLogEntries(FILE_NAME)
        }
    }

    override fun getString(string: Int) : String {
        return IAApp.instance.getString(string)
    }

    override fun onDestroy() {
        // Todo Anything to do here?
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadZipFileTask : AsyncTask<ResponseBody, Pair<Int, Long>, String?>() {

        override fun doInBackground(vararg urls: ResponseBody): String? {
            //Copy you logic to calculate progress and call
            saveToDisk(urls[0], FILE_NAME)
            return null
        }

        override fun onProgressUpdate(vararg progress: Pair<Int, Long>) {

            Log.d("API123", progress[0].second.toString() + " ")

            if (progress[0].first == 100)
                Toast.makeText(IAApp.instance, "File downloaded successfully", Toast.LENGTH_SHORT).show()


            if (progress[0].second > 0) {
                val currentProgress = (progress[0].first.toDouble() / progress[0].second.toDouble() * 100).toInt()

                sendProgressUpdate(TASK_DOWNLOAD, "$currentProgress")
            }

            if (progress[0].first == -1) {
                logCallback.onFileDownloadFailure()
            }
        }

        fun doProgress(progressDetails: Pair<Int, Long>) {
            publishProgress(progressDetails)
        }

        override fun onPostExecute(result: String?) {
            onFileDownloaded()
        }
    }

    private fun saveToDisk(body: ResponseBody, filename: String) {
        try {
            val destinationFile =
                File(IAApp.instance.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = body.byteStream()
                outputStream = FileOutputStream(destinationFile)

                val data = ByteArray(4096)
                var count: Int
                var progress = 0
                val fileSize = body.contentLength()
                var reading = true

                do {
                    count = inputStream!!.read(data)
                    if (count == -1){
                        reading = false
                    }

                    if (reading){
                        outputStream.write(data, 0, count)
                        progress += count
                        val pairs = Pair(progress, fileSize)
                        downloadZipFileTask.doProgress(pairs)
                    }
                } while (reading)

                outputStream.flush()

                val pairs = Pair(100, 100L)
                downloadZipFileTask.doProgress(pairs)


                return
            } catch (e: IOException) {
                e.printStackTrace()
                val pairs = Pair(-1, java.lang.Long.valueOf(-1))
                downloadZipFileTask.doProgress(pairs)
//                Log.d(TAG, "Failed to save the file!")
                return
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }

    @Suppress("LocalVariableName")
    private fun readAndSortLogEntries(filename : String){
        val USER_IP_INDEX = 0
        val ENDPOINT_INDEX = 6

        sendProgressUpdate(TASK_READ, "0")

        val file = File(IAApp.instance.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
        val lines: List<String> = file.readLines()

        totalSize = lines.size
        nLogN = (Math.log(totalSize.toDouble()) * totalSize).toInt()
        var progressCount = 1

        val userSequenceMap : MutableMap<String, MutableList<String>> = HashMap()
        val sequenceCountMap : MutableMap<String, Int> = HashMap()

        for (line in lines) {
            if (progressCount % 5 == 0) {
                sendProgressUpdate(TASK_READ, ((progressCount.toDouble() / totalSize.toDouble()) * 100).toInt().toString())
            }

            val data : List<String> = line.split(" ")
            val user = data[USER_IP_INDEX]
            val endPoint = data[ENDPOINT_INDEX]

            if (userSequenceMap.containsKey(user)){
                userSequenceMap[user]?.add(endPoint)
            } else {
                userSequenceMap[user] = mutableListOf(endPoint)
            }

            if (userSequenceMap[user]?.size!! >= 3){
                val firstIndex = userSequenceMap[user]?.size!! - 3
                val sequenceKey = "${userSequenceMap[user]?.get(firstIndex)}_${userSequenceMap[user]?.get(firstIndex + 1)}_${userSequenceMap[user]?.get(firstIndex + 2)}"

                if (sequenceCountMap.containsKey(sequenceKey)){
                    sequenceCountMap[sequenceKey] = sequenceCountMap[sequenceKey]!! + 1
                } else {
                    sequenceCountMap[sequenceKey] = 1
                }
            }

            progressCount++
        }

        sendProgressUpdate(TASK_READ, "100")

        sortSequenceMap(sequenceCountMap)
    }

    /**
     * Going with a Merge Sort for this. I am not so concerned with in-place sorting for this demo app and Merge Sort
     * worst case is O(N log N) whereas Quick Sort worst case is O(N^2). If we decided memory usage was a main concern,
     * this would likely be a Quick Sort instead.
     */
    private fun sortSequenceMap(sequenceCountMap : MutableMap<String, Int>){
        val sequenceArray = sequenceCountMap.entries.toList()
        CoroutineScope(Dispatchers.Main).launch {
            logCallback.onLogsApiSuccess(mergeSort(sequenceArray))
        }
            sendProgressUpdate(TASK_SORT, "100")
    }

    private fun mergeSort(list: List<MutableMap.MutableEntry<String, Int>>): List<MutableMap.MutableEntry<String, Int>> {
        if (list.size <= 1) {
            return list
        }

        val middle = list.size / 2
        val left = list.subList(0,middle)
        val right = list.subList(middle,list.size)

        return merge(mergeSort(left), mergeSort(right))
    }

    private fun merge(left: List<MutableMap.MutableEntry<String, Int>>, right: List<MutableMap.MutableEntry<String, Int>>): List<MutableMap.MutableEntry<String, Int>>  {
        var indexLeft = 0
        var indexRight = 0
        val newList : MutableList<MutableMap.MutableEntry<String, Int>> = mutableListOf()

        while (indexLeft < left.count() && indexRight < right.count()) {
            if (left[indexLeft].value >= right[indexRight].value) {
                newList.add(left[indexLeft])
                indexLeft++
                stepCount++
            } else {
                newList.add(right[indexRight])
                indexRight++
                stepCount++
            }
        }

        while (indexLeft < left.size) {
            newList.add(left[indexLeft])
            indexLeft++
            stepCount++
        }

        while (indexRight < right.size) {
            newList.add(right[indexRight])
            indexRight++
            stepCount++
        }

        sendProgressUpdate(TASK_SORT, ((stepCount.toDouble()/nLogN.toDouble()) * 100).toInt().toString())
        return newList
    }


    private fun sendProgressUpdate(task: String, progress: String){
        CoroutineScope(Dispatchers.Main).launch {
            logCallback.onUpdateProgress(task, progress)
        }
    }
}