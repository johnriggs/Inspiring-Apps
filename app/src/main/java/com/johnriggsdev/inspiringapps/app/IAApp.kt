package com.johnriggsdev.inspiringapps.app

import android.app.Application
import com.johnriggsdev.inspiringapps.di.components.ApplicationComponent
import com.johnriggsdev.inspiringapps.di.components.DaggerApplicationComponent
import com.johnriggsdev.inspiringapps.di.modules.ApplicationModule

class IAApp : Application() {

    private lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        setup()

    }

    // Method call currently appears deprecated only because the ApplicationComponent is not called in the project
    // at this time. That can change in the future.
    @Suppress("deprecation")
    private fun setup() {
        component = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this)).build()
        component.inject(this)
    }

    @Suppress("unused")
    fun getApplicationComponent(): ApplicationComponent {
        return component
    }

    companion object {
        lateinit var instance: IAApp private set
    }
}