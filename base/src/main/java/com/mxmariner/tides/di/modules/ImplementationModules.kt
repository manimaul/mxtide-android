package com.mxmariner.tides.di.modules

import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.di.scopes.FragmentScope
import com.mxmariner.tides.main.util.RxLocation
import com.mxmariner.tides.main.util.RxLocationImpl
import com.mxmariner.tides.main.util.RxPermission
import com.mxmariner.tides.main.util.RxPermissionImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * [Singleton] scope protocol implementations
 */
@Module
interface ImplementationModule


/**
 * Binds [ActivityScope] scope protocol implementations
 */
@Module
interface ActivityImplementationBinderModule {

    @ActivityScope
    @Binds
    fun bindRxLocation(rxLocationImpl: RxLocationImpl): RxLocation

    @Binds
    fun bindRxPermission(rxPermissionImpl: RxPermissionImpl): RxPermission
}

/**
 * [FragmentScope] scope protocol implementations
 */
@Module
interface FragmentImplementationModule
