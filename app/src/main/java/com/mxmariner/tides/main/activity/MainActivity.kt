package com.mxmariner.tides.main.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mxmariner.tides.R
import com.mxmariner.tides.currents.fragment.CurrentsFragment
import com.mxmariner.tides.main.util.PerfTimer
import com.mxmariner.tides.map.fragment.MapFragment
import com.mxmariner.tides.settings.fragment.SettingsFragment
import com.mxmariner.tides.tides.fragment.TidesFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject lateinit var dispatchingInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingInjector
    }

    private val bottomNavigationHandler = { item: MenuItem ->
        when (item.itemId) {
            R.id.navigation_tides -> TidesFragment()
            R.id.navigation_currents -> CurrentsFragment()
            R.id.navigation_map -> MapFragment()
            R.id.navigation_settings -> SettingsFragment()
            else -> null
        }?.let {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, it)
                    .commit()
            true
        } ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        PerfTimer.markEventStop("Between")
        PerfTimer.markEventStart("MainActivity.onCreate()")

        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(bottomNavigationHandler)

        PerfTimer.markEventStop("MainActivity.onCreate()")
        PerfTimer.printLogOfCapturedEvents(true)
    }
}
