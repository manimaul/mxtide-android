package com.mxmariner.tides.di.components

import com.mxmariner.tides.di.modules.ActivityBinderModule
import com.mxmariner.tides.di.modules.AndroidModule
import com.mxmariner.tides.di.modules.FragmentBinderModule
import com.mxmariner.tides.main.application.MxTidesApplication
import dagger.Component


@Component(modules = [AndroidModule::class,
    ActivityBinderModule::class,
    FragmentBinderModule::class])
abstract class ApplicationComponent {
    abstract fun inject(application: MxTidesApplication)
}
