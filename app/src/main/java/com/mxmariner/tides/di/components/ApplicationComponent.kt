package com.mxmariner.tides.di.components

import com.mxmariner.tides.di.modules.ActivityModule
import com.mxmariner.tides.di.modules.AndroidModule
import com.mxmariner.tides.main.application.MxTidesApplication
import dagger.Component


@Component(modules = [AndroidModule::class, ActivityModule::class])
abstract class ApplicationComponent {
    abstract fun inject(application: MxTidesApplication)
}
