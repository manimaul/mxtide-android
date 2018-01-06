package com.mxmariner.tides.main.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.mxmariner.tides.R
import com.mxmariner.tides.main.util.PerfTimer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_tides -> {
                message.setText(R.string.title_tides)
                true
            }
            R.id.navigation_currents -> {
                message.setText(R.string.title_currents)
                true
            }
            R.id.navigation_settings -> {
                message.setText(R.string.title_settings)
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        PerfTimer.markEventStop("Between")
        PerfTimer.markEventStart("MainActivity.onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        PerfTimer.markEventStop("MainActivity.onCreate()")
        PerfTimer.printLogOfCapturedEvents(true)
    }
}
