package com.mxmariner.tides.main.fragment

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.mxmariner.tides.main.R
import com.mxmariner.tides.main.di.MainModuleInjector


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.let {
            val injector = MainModuleInjector.activityScopeAssembly(it)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}