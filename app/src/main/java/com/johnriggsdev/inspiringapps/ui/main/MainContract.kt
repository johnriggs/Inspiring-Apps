package com.johnriggsdev.inspiringapps.ui.main

import com.johnriggsdev.inspiringapps.BaseContract

interface MainContract {
    interface View: BaseContract.View {
        fun setupRecyclerView(sequences: List<MutableMap.MutableEntry<String, Int>>)
        fun showProgressBar(visibile: Boolean)
        fun showProgressText(visibile: Boolean)
        fun showErrorToast(message: String)
        fun updateProgressText(text: String)
    }

    interface Presenter: BaseContract.Presenter<MainContract.View>

    interface Repository: BaseContract.Repository {
        fun fetchLogFileFromApi()
        fun setNetworkCallback(callback: LogDataCallback)
    }
}