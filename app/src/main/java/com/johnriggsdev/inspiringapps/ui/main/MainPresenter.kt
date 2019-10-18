package com.johnriggsdev.inspiringapps.ui.main

import com.johnriggsdev.inspiringapps.R
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.TASK_SORT

class MainPresenter(private val repo: MainContract.Repository): MainContract.Presenter, LogDataCallback {

    private var view : MainContract.View? = null

    override fun attach(view: MainContract.View) {
        this.view = view
        repo.setNetworkCallback(this)
        repo.fetchLogFileFromApi()
        initializeViews()
    }

    override fun onDestroy(){
        this.view = null
    }

    override fun onApiError(message: String) {
        view?.showErrorToast(message)
    }

    override fun onLogsApiSuccess(sequences : List<MutableMap.MutableEntry<String, Int>>) {
        view?.setupRecyclerView(sequences)

        view?.showProgressBar(false)
    }

    override fun onUpdateProgress(task: String, progress: String) {
        if (task == TASK_SORT && progress == "100"){
            view?.updateProgressText("")
        } else {
            view?.updateProgressText("$task: $progress%")
        }
    }

    override fun onFileReadFailure() {
        view?.showErrorToast(repo.getString(R.string.read_log_failure))
    }

    override fun onFileDownloadFailure() {
        view?.showErrorToast(repo.getString(R.string.download_log_failure))
    }

    private fun initializeViews(){
        view?.showProgressBar(true)
    }

    // For Testing
    fun getView() : MainContract.View? {
        return view
    }
}

interface LogDataCallback {
    fun onFileReadFailure()
    fun onFileDownloadFailure()
    fun onLogsApiSuccess(sequences : List<MutableMap.MutableEntry<String, Int>>)
    fun onUpdateProgress(task: String, progress : String)
    fun onApiError(message: String)
}