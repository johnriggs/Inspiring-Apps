package com.johnriggsdev.inspiringapps.ui.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.johnriggsdev.inspiringapps.R
import android.widget.Toast
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast.LENGTH_LONG
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnriggsdev.inspiringapps.di.components.DaggerActivityComponent
import com.johnriggsdev.inspiringapps.di.modules.ActivityModule
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        injectDependency()
        presenter.attach(this)

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101)
    }

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    private fun askForPermission(permission: String, requestCode: Int?) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, permission)) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode!!)

            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode!!)
            }
        } else if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(applicationContext, "Permission was denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun updateProgressText(text: String) {
        main_progress_text.text = text
    }

    override fun setupRecyclerView(sequences: List<MutableMap.MutableEntry<String, Int>>) {
        recycler_main.layoutManager = LinearLayoutManager(this)
        recycler_main.adapter = MainAdapter(sequences, this)
        recycler_main.visibility = View.VISIBLE
    }

    override fun showProgressBar(visibile: Boolean) {
        main_progress.visibility = if (visibile) View.VISIBLE else View.INVISIBLE
    }

    override fun showProgressText(visibile: Boolean) {
        main_progress_text.visibility = if (visibile) View.VISIBLE else View.GONE
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(this, message, LENGTH_LONG).show()
    }
}
