package com.mxmariner.tides.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.github.salomonbrys.kodein.instance
import com.mxmariner.tides.main.R
import com.mxmariner.tides.main.activity.LocationSearchActivity
import com.mxmariner.tides.main.di.MainModuleInjector
import com.mxmariner.tides.util.RxActivityResult
import com.mxmariner.tides.util.RxSharedPrefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedPreferences: RxSharedPrefs
    private lateinit var rxActivityResult: RxActivityResult
    private lateinit var ctx: Context
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.let {
            val injector = MainModuleInjector.activityScopeAssembly(it)
            sharedPreferences = injector.instance()
            rxActivityResult = injector.instance()
            ctx = injector.instance()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = getString(R.string.PREF_KEY_LOCATION)
        compositeDisposable.add(sharedPreferences.observeChanges<String>(key)
                .filter { it == getString(R.string.location_user) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pickUserLocation() })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    fun pickUserLocation() {
        val intent = Intent(ctx, LocationSearchActivity::class.java)
        compositeDisposable.add(rxActivityResult.startActivityForResultSingle(intent)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {

                })
    }
}