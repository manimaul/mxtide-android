package com.mxmariner.tides.di.modules

import android.app.Activity
import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.di.scopes.FragmentScope
import com.mxmariner.tides.main.activity.MainActivity
import com.mxmariner.tides.main.routing.EmptyRouter
import com.mxmariner.tides.main.routing.MainActivityRoutes
import com.mxmariner.tides.main.routing.Router
import com.mxmariner.tides.main.util.RxLocation
import com.mxmariner.tides.main.util.RxLocationImpl
import com.mxmariner.tides.main.util.RxPermission
import com.mxmariner.tides.main.util.RxPermissionImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * [Singleton] scope protocol implementations
 */
@Module
interface ImplementationModule

/**
 * Provides [ActivityScope] scope protocol implementations
 */
@Module
abstract class ActivityImplementationModule {

    @ActivityScope
    @Provides
    fun provideRouter(activity: Activity) : Router {
        return when (activity) {
            is MainActivity -> activity
            else -> EmptyRouter()
        }
    }
}

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
