package com.johnriggsdev.inspiringapps.di.modules

import androidx.appcompat.app.AppCompatActivity
import com.johnriggsdev.inspiringapps.ui.main.MainContract
import com.johnriggsdev.inspiringapps.ui.main.MainPresenter
import com.johnriggsdev.inspiringapps.ui.main.MainRepository
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private var activity: AppCompatActivity) {
    @Provides
    fun provideActivity(): AppCompatActivity {
        return activity
    }

    @Provides
    fun providePresenter(): MainContract.Presenter {
        return MainPresenter(provideRepository())
    }

    @Provides
    fun provideRepository(): MainContract.Repository {
        return MainRepository()
    }
}