package com.mxmariner.tides.di.modules

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.mxmariner.tides.currents.fragment.CurrentsFragment
import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.di.scopes.FragmentScope
import com.mxmariner.tides.main.activity.MainActivity
import com.mxmariner.tides.map.fragment.MapFragment
import com.mxmariner.tides.settings.fragment.SettingsFragment
import com.mxmariner.tides.tides.fragment.TidesFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal interface ActivityBinderModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityAndroidModule::class,
        FragmentBinderModule::class,
        ActivityAndroidModule::class,
        ActivityImplementationBinderModule::class])
    fun mainActivity(): MainActivity

    @Binds
    fun appCompatActivity(mainActivity: MainActivity): AppCompatActivity

    @Binds
    fun activity(mainActivity: MainActivity): Activity
}

@Module
internal interface FragmentBinderModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [FragmentAndroidModule::class,
        FragmentImplementationModule::class])
    fun tideFragment(): TidesFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FragmentAndroidModule::class,
        FragmentImplementationModule::class])
    fun mapFragment(): MapFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FragmentAndroidModule::class,
        FragmentImplementationModule::class])
    fun currentsFragment(): CurrentsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FragmentAndroidModule::class,
        FragmentImplementationModule::class])
    fun settingsFragment(): SettingsFragment
}
