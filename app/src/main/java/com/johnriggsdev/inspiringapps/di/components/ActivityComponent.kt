package com.johnriggsdev.inspiringapps.di.components

import com.johnriggsdev.inspiringapps.di.modules.ActivityModule
import com.johnriggsdev.inspiringapps.ui.main.MainActivity
import com.johnriggsdev.inspiringapps.ui.main.MainPresenter
import dagger.Component

@Component(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(mainPresenter: MainPresenter)
}