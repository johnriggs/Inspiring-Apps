package com.johnriggsdev.inspiringapps

interface BaseContract {
    interface Presenter<in T> {
        fun attach(view :T)
        fun onDestroy()
    }

    interface View

    interface Repository {
        fun getString(string: Int) : String
        fun onDestroy()
    }
}