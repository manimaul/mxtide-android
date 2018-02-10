package com.mxmariner.tides.main.fragment

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.mxmariner.tides.main.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        Injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}