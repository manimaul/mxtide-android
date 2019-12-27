package com.mxmariner.tides.globe.di

import androidx.fragment.app.FragmentActivity
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import com.mxmariner.tides.di.Injector
import com.mxmariner.tides.globe.util.GlobePreferences
import com.mxmariner.tides.globe.util.ShapeFileDao
import com.mxmariner.tides.globe.viewmodel.GlobeViewModel
import com.mxmariner.tides.globe.viewmodel.GlobeViewModelFactory

object GlobeModuleInjector {

    init {
        Injector.mixInActivityScope(Kodein.Module {
            bind() from provider { GlobeViewModel(this) }
            bind() from provider { GlobeViewModelFactory(this) }
            bind() from provider { GlobePreferences(this) }
            bind() from provider { ShapeFileDao(this) }
        })
    }

    /**
     * Gets the [FragmentActivity] scope assembly for the specified activity creating it if necessary
     * which mixes in this project's assembly module(s).
     */
    fun activityScopeAssembly(activity: FragmentActivity) : Kodein {
        return Injector.activityScopeAssembly(activity)
    }
}