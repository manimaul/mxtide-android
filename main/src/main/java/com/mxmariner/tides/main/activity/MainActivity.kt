package com.mxmariner.tides.main.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.main.R
import com.mxmariner.tides.main.di.MainModuleInjector
import com.mxmariner.tides.main.fragment.SettingsFragment
import com.mxmariner.tides.main.fragment.TidesFragment
import com.mxmariner.tides.util.PerfTimer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fm: FragmentManager

    private val bottomNavigationHandler = { item: MenuItem ->
        routeToTab(item.itemId)
    }

    //region AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        PerfTimer.markEventStop("Between")
        PerfTimer.markEventStart("MainActivity.onCreate()")

        val injector = MainModuleInjector.activityScopeAssembly(this)
        fm = injector.instance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(bottomNavigationHandler)
        navigation.inflateMenu(R.menu.navigation)

        setSupportActionBar(toolbar)

        PerfTimer.markEventStop("MainActivity.onCreate()")
        PerfTimer.printLogOfCapturedEvents(true)

        selectTabFromUri(intent?.data)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        selectTabFromUri(intent?.data)
    }

    //endregion

    private fun selectTabFromUri(uri: Uri?) {
        uri?.getQueryParameter("tab")?.let {
            when (it) {
                "nearby_tides" -> navigation.selectedItemId = R.id.navigation_tides
                "nearby_currents" -> navigation.selectedItemId = R.id.navigation_currents
                "settings" -> navigation.selectedItemId = R.id.navigation_settings
            }
        } ?: {
            navigation.selectedItemId = R.id.navigation_tides
        }()
    }

    private fun routeToTab(@IdRes id: Int) : Boolean {
        return when (id) {
            R.id.navigation_tides -> TidesFragment.create(StationType.TIDES)
            R.id.navigation_currents -> TidesFragment.create(StationType.CURRENTS)
            R.id.navigation_settings -> SettingsFragment()
            else -> null
        }?.let {
            fm.beginTransaction()
                    .replace(R.id.container, it)
                    .commit()
            true
        } ?: false
    }

}
