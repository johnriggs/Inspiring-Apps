package com.johnriggsdev.inspiringapps.di.modules

import android.app.Application
import com.johnriggsdev.inspiringapps.app.IAApp
import com.johnriggsdev.inspiringapps.di.scopes.PerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val iaApp: IAApp) {

    @Provides
    @Singleton
    @PerApplication
    fun provideAllication() : Application {
        return iaApp
    }
}