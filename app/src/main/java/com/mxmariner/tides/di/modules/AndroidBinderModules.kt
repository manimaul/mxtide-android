package com.mxmariner.tides.di.modules

import com.mxmariner.tides.currents.fragment.CurrentsFragment
import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.di.scopes.FragmentScope
import com.mxmariner.tides.main.activity.MainActivity
import com.mxmariner.tides.map.fragment.MapFragment
import com.mxmariner.tides.settings.fragment.SettingsFragment
import com.mxmariner.tides.tides.fragment.TidesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBinderModule {
    @ActivityScope
    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity
}

@Module
internal abstract class FragmentBinderModule {
    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun tideFragment(): TidesFragment

    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun mapFragment(): MapFragment

    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun currentsFragment(): CurrentsFragment

    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun settingsFragment(): SettingsFragment
}