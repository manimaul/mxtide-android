package com.mxmariner.tides.settings.fragment

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.mxmariner.tides.R
import dagger.android.support.AndroidSupportInjection


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}