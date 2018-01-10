package com.mxmariner.tides.di.modules

import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.di.scopes.FragmentScope
import com.mxmariner.tides.main.util.RxLocation
import com.mxmariner.tides.main.util.RxLocationImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * [Singleton] scope protocol implementations
 */
@Module
interface ImplementationModule

/**
 * [ActivityScope] scope protocol implementations
 */
@Module
interface ActivityImplementationModule {
    @Binds
    fun bindRxLocation(rxLocationImpl: RxLocationImpl): RxLocation
}


/**
 * [FragmentScope] scope protocol implementations
 */
@Module
interface FragmentImplementationModule