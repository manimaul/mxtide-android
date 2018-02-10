package com.mxmariner.tides.di.modules

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.mxmariner.tides.currents.fragment.CurrentsFragment
import com.mxmariner.tides.details.activity.DetailsActivity
import com.mxmariner.tides.di.Injector
import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.di.scopes.FragmentScope
import com.mxmariner.tides.main.activity.MainActivity
import com.mxmariner.tides.map.fragment.MapFragment
import com.mxmariner.tides.settings.fragment.SettingsFragment
import com.mxmariner.tides.tides.fragment.TidesFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module class ActivityModule {

    @Provides
    fun provideActivity() : Activity {
        return Injector.foregroundActivity
    }

    @Provides
    fun provideAppCompatActivity() : AppCompatActivity {
        return Injector.foregroundActivity
    }
 }

@Module
internal interface ActivityBinderModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityAndroidModule::class,
        FragmentBinderModule::class,
        ActivityImplementationBinderModule::class])
    fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityAndroidModule::class,
        FragmentBinderModule::class,
        ActivityImplementationBinderModule::class])
    fun detailsActivity(): DetailsActivity
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
