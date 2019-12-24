package com.mxmariner.tides.globe.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.globe.fragment.GlobeFragment
import com.mxmariner.tides.globe.R
import kotlinx.android.synthetic.main.globe_layout.*

class GlobeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.globe_layout)
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, GlobeFragment())
                .commitNow()

        navigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_tides -> {
                    supportFragmentManager.findFragmentById(R.id.content).takeIf {
                        it is GlobeFragment
                    }?.let {
                        (it as GlobeFragment).select(StationType.TIDES)
                    }
                }
                R.id.navigation_currents -> {
                    supportFragmentManager.findFragmentById(R.id.content).takeIf {
                        it is GlobeFragment
                    }?.let {
                        (it as GlobeFragment).select(StationType.CURRENTS)
                    }
                }
            }
            true
        }
    }
}