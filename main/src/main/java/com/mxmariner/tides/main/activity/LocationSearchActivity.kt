package com.mxmariner.tides.main.activity

import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.github.salomonbrys.kodein.instance
import com.mxmariner.tides.main.R
import com.mxmariner.tides.main.di.MainModuleInjector
import kotlinx.android.synthetic.main.activity_search_layout.*

class LocationSearchActivity : AppCompatActivity() {

    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val injector = MainModuleInjector.activityScopeAssembly(this)
        geocoder = injector.instance()

        setContentView(R.layout.activity_search_layout)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.search)?.apply {
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    finish()
                    return false
                }

            })
            expandActionView()
        }
        return super.onPrepareOptionsMenu(menu)
    }
}
