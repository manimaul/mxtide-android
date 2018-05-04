package com.mxmariner.tides.main.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.github.salomonbrys.kodein.instance
import com.google.android.instantapps.InstantApps
import com.mxmariner.tides.main.R
import com.mxmariner.tides.main.activity.LocationSearchActivity
import com.mxmariner.tides.main.di.MainModuleInjector
import com.mxmariner.tides.routing.RouteSettings
import com.mxmariner.tides.util.RxActivityResult
import com.mxmariner.tides.util.RxSharedPrefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var rxSharedPreferences: RxSharedPrefs
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rxActivityResult: RxActivityResult
    private lateinit var ctx: Context
    private lateinit var locationPreference: ListPreference
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.let {
            val injector = MainModuleInjector.activityScopeAssembly(it)
            rxSharedPreferences = injector.instance()
            rxActivityResult = injector.instance()
            sharedPreferences = injector.instance()
            ctx = injector.instance()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = getString(com.mxmariner.tides.R.string.PREF_KEY_LOCATION)
        compositeDisposable.add(rxSharedPreferences.observeChanges<String>(key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == getString(com.mxmariner.tides.R.string.location_user)) {
                        pickUserLocation()
                    } else {
                        updateLocationPrefSummary()
                    }
                })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val key = getString(com.mxmariner.tides.R.string.PREF_KEY_LOCATION)
        locationPreference = findPreference(key) as ListPreference

        if (InstantApps.isInstantApp(ctx)) {
            findPreference(getString(R.string.PREF_KEY_INSTALL))?.setOnPreferenceClickListener {
                activity?.let {
                    val intent = Intent(Intent.ACTION_VIEW, RouteSettings().uri)
                    intent.`package` =  it.packageName
                    InstantApps.showInstallPrompt(it, intent, 9000, null)
                }
                true
            }
        } else {
            findPreference(getString(R.string.PREF_KEY_INSTALL_CAT))?.isVisible = false
            findPreference(getString(R.string.PREF_KEY_INSTALL))?.isVisible = false
        }
        updateLocationPrefSummary()
    }

    private fun updateLocationPrefSummary() {
        sharedPreferences.getString(locationPreference.key, null)?.let {
            locationPreference.summary = it
        }
    }

    private fun pickUserLocation() {
        val intent = Intent(ctx, LocationSearchActivity::class.java)
        compositeDisposable.add(rxActivityResult.startActivityForResultSingle(intent)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    val addressKey = "address"

                    val address = it.data?.takeIf {
                        it.hasExtra(addressKey)
                    }?.getParcelableExtra<Address>(addressKey)?.takeIf {
                        it.hasLatitude() && it.hasLongitude()
                    }

                    address?.let {
                        val value = "${address.featureName}:${address.latitude}:${address.longitude}"
                        locationPreference.value = value
                    } ?: {
                        val defaultValue = getString(com.mxmariner.tides.R.string.location_device)
                        locationPreference.value = defaultValue
                    }()
                })
    }
}