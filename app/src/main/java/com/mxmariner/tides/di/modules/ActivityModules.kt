package com.mxmariner.tides.di.modules

import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.main.activity.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector //supply activity sub-components here
    internal abstract fun mainActivity(): MainActivity
}