package com.mxmariner.tides.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.instantapps.InstantApps
import com.mxmariner.di.Injector
import com.mxmariner.tides.BuildConfig
import com.mxmariner.tides.R
import com.mxmariner.tides.routing.RouteLocationSearch
import com.mxmariner.tides.routing.RouteSettings
import com.mxmariner.tides.routing.Router
import com.mxmariner.tides.util.RxActivityResult
import com.mxmariner.tides.util.RxSharedPrefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject lateinit var rxSharedPreferences: RxSharedPrefs
    @Inject lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var rxActivityResult: RxActivityResult
    @Inject lateinit var ctx: Context
    @Inject lateinit var router: Router
    private lateinit var locationPreference: ListPreference
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        requireActivity().let {
            Injector.activityInjector(it).inject(this)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = getString(R.string.PREF_KEY_LOCATION)
        compositeDisposable.add(rxSharedPreferences.observeChanges<String>(key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == getString(R.string.location_user)) {
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
        val key = getString(R.string.PREF_KEY_LOCATION)
        locationPreference = findPreference(key) as ListPreference
        findPreference(getString(R.string.PREF_KEY_VERSION)).title = "${BuildConfig.VERSION_NAME} vc ${BuildConfig.VERSION_CODE} ${BuildConfig.BUILD_TYPE}"

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
        compositeDisposable.add(router.routeToForResult(RouteLocationSearch())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { result ->
                    val addressKey = "address"

                    val address = result.data?.takeIf { intent ->
                        intent.hasExtra(addressKey)
                    }?.getParcelableExtra<Address>(addressKey)?.takeIf { address ->
                        address.hasLatitude() && address.hasLongitude()
                    }

                    address?.let {
                        val value = "${address.featureName}:${address.latitude}:${address.longitude}"
                        locationPreference.value = value
                    } ?: {
                        val defaultValue = getString(R.string.location_device)
                        locationPreference.value = defaultValue
                    }()
                })
    }
}