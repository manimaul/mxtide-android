package com.mxmariner.tides.main.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.mxmariner.tides.main.di.MainModuleInjector

class MapFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.let {
            val injector = MainModuleInjector.activityScopeAssembly(it)
        }
        super.onCreate(savedInstanceState)
    }
}