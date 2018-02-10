package com.mxmariner.tides.main.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mxmariner.tides.R
import com.mxmariner.tides.currents.fragment.CurrentsFragment
import com.mxmariner.tides.di.Injector
import com.mxmariner.tides.main.routing.routerIntentAction
import com.mxmariner.tides.main.util.PerfTimer
import com.mxmariner.tides.map.fragment.MapFragment
import com.mxmariner.tides.settings.fragment.SettingsFragment
import com.mxmariner.tides.tides.fragment.TidesFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject lateinit var dispatchingInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var fragmentManager: FragmentManager

    private val bottomNavigationHandler = { item: MenuItem ->
        routeToTab(item.itemId)
    }

    //region HasSupportFragmentInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingInjector
    }

    //endregion

    //region AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        PerfTimer.markEventStop("Between")
        PerfTimer.markEventStart("MainActivity.onCreate()")

        Injector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(bottomNavigationHandler)

        setSupportActionBar(toolbar)

        fragmentManager.beginTransaction()
                .replace(R.id.container, TidesFragment())
                .commit()

        PerfTimer.markEventStop("MainActivity.onCreate()")
        PerfTimer.printLogOfCapturedEvents(true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == routerIntentAction) {
            when (intent.data.path) {
                    "/main/nearby_tides" -> routeToTab(R.id.navigation_tides)
                    "/main/nearby_currents" -> routeToTab(R.id.navigation_currents)
                    "/main/map" -> routeToTab(R.id.navigation_map)
                    "/main/settings" -> routeToTab(R.id.navigation_settings)
            }
        }
    }

    //endregion

    private fun routeToTab(@IdRes id: Int) : Boolean {
        return when (id) {
            R.id.navigation_tides -> TidesFragment()
            R.id.navigation_currents -> CurrentsFragment()
            R.id.navigation_map -> MapFragment()
            R.id.navigation_settings -> SettingsFragment()
            else -> null
        }?.let {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, it)
                    .commit()
            true
        } ?: false
    }

}
