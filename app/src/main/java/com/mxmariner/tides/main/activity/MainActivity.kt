package com.mxmariner.tides.main.activity

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mxmariner.tides.R
import com.mxmariner.tides.currents.fragment.CurrentsFragment
import com.mxmariner.tides.main.routing.MainActivityRoutes
import com.mxmariner.tides.main.routing.Route
import com.mxmariner.tides.main.routing.Router
import com.mxmariner.tides.main.util.PerfTimer
import com.mxmariner.tides.map.fragment.MapFragment
import com.mxmariner.tides.settings.fragment.SettingsFragment
import com.mxmariner.tides.tides.fragment.TidesFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, Router {

    @Inject lateinit var dispatchingInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var fragmentManager: FragmentManager

    private val bottomNavigationHandler = { item: MenuItem ->
        when (item.itemId) {
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

    private fun routeToTab(@IdRes id: Int) {
        navigation.selectedItemId = id
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

        AndroidInjection.inject(this)
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

    //endregion

    //region Router

    override fun <T : Serializable> routeTo(route: Route<T>) {
        when (route) {
            is MainActivityRoutes.NearbyTides -> routeToTab(R.id.navigation_tides)
            is MainActivityRoutes.Map -> routeToTab(R.id.navigation_currents)
            is MainActivityRoutes.NearbyCurrents -> routeToTab(R.id.navigation_currents)
            is MainActivityRoutes.Settings -> routeToTab(R.id.navigation_settings)
        }
    }

    override fun <T : Serializable, R> routeToForResult(route: Route<T>): Single<R> {
        return Single.error(Throwable("Route Not Found"))
    }

    override fun back() {
        finish()
    }

    //endregion

}
