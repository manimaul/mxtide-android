package com.mxmariner.main.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.mxmariner.di.Injector
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.fragment.SettingsFragment
import com.mxmariner.tides.fragment.TidesFragment
import com.mxmariner.tides.routing.RouteGlobe
import com.mxmariner.tides.routing.Router
import com.mxmariner.tides.util.PerfTimer
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@Keep
class MainActivity : AppCompatActivity() {

    @Inject lateinit var fm: FragmentManager
    @Inject lateinit var router: Router


    //region AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        PerfTimer.markEventStop("Between")
        PerfTimer.markEventStart("MainActivity.onCreate()")

        Injector.activityInjector(this).inject(this);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            routeToTab(item.itemId)
        }
        setSupportActionBar(toolbar)
        PerfTimer.markEventStop("MainActivity.onCreate()")
        PerfTimer.printLogOfCapturedEvents(true)

        selectTabFromUri(intent?.data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_navigation, menu)
        menu?.findItem(R.id.navigation_globe)?.setOnMenuItemClickListener {
            router.routeTo(RouteGlobe())
            true
        }
        return true
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

    private fun routeToTab(@IdRes id: Int): Boolean {
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
