package com.mxmariner.tides.di.components

import android.app.Application
import com.mxmariner.tides.di.modules.ActivityBinderModule
import com.mxmariner.tides.di.modules.AndroidModule
import com.mxmariner.tides.di.modules.FragmentBinderModule
import com.mxmariner.tides.main.application.MxTidesApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidModule::class,
    ActivityBinderModule::class,
    FragmentBinderModule::class])
internal interface ApplicationComponent {

    @Component.Builder
     interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    fun inject(application: MxTidesApplication)
}
