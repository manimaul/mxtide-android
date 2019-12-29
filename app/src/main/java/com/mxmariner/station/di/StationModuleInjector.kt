package com.mxmariner.station.di

import androidx.fragment.app.FragmentActivity
import com.github.salomonbrys.kodein.Kodein
import com.mxmariner.tides.di.Injector

object StationModuleInjector {

    /**
     * Gets the [FragmentActivity] scope assembly for the specified activity creating it if necessary
     * which mixes in this project's assembly module(s).
     */
    fun activityScopeAssembly(activity: FragmentActivity) : Kodein {
        return Injector.activityScopeAssembly(activity)
    }
}