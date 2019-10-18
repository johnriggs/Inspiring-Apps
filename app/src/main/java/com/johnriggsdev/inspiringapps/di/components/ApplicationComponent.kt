package com.johnriggsdev.inspiringapps.di.components

import com.johnriggsdev.inspiringapps.app.IAApp
import com.johnriggsdev.inspiringapps.di.modules.ApplicationModule
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(application: IAApp)
}