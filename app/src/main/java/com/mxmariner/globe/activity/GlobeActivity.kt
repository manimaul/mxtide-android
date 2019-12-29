package com.mxmariner.globe.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.github.salomonbrys.kodein.instance
import com.mxmariner.globe.di.GlobeModuleInjector
import com.mxmariner.globe.fragment.GlobeFragment
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.util.RxLocation
import kotlinx.android.synthetic.main.globe_layout.*

@Keep
class GlobeActivity : AppCompatActivity() {

    lateinit var rxLocation: RxLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        val injector = GlobeModuleInjector.activityScopeAssembly(this)
        rxLocation = injector.instance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.globe_layout)
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, GlobeFragment())
                .commitNow()

        globeFragment?.let {
            when(it.typeSelected) {
                StationType.TIDES -> navigation.selectedItemId = R.id.navigation_tides
                StationType.CURRENTS -> navigation.selectedItemId = R.id.navigation_currents
            }
        }

        toolbar.menu?.findItem(R.id.set_location)?.setOnMenuItemClickListener {
            rxLocation.lastKnownLocation?.let {
                globeFragment?.userSelectLocation(it)
            }
            true
        }

        navigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_tides -> {
                    globeFragment?.select(StationType.TIDES)
                }
                R.id.navigation_currents -> {
                    globeFragment?.select(StationType.CURRENTS)
                }
            }
            true
        }
    }

    private val globeFragment: GlobeFragment?
        get() = supportFragmentManager.findFragmentById(R.id.content).takeIf {
            it is GlobeFragment
        }?.let {
            (it as GlobeFragment)
        }
}