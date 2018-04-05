package com.mxmariner.tides.main.activity

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.github.salomonbrys.kodein.instance
import com.mxmariner.tides.extensions.evaluateNullables
import com.mxmariner.tides.main.R
import com.mxmariner.tides.main.di.MainModuleInjector
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_layout.*
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

class LocationSearchActivity : AppCompatActivity() {

    companion object {
        val TAG = LocationSearchActivity::class.java.simpleName
    }

    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val injector = MainModuleInjector.activityScopeAssembly(this)
        geocoder = injector.instance()

        setContentView(R.layout.activity_search_layout)
        setSupportActionBar(toolbar)
    }

    private fun addressSelection(query: String?, address: Address?) {
        evaluateNullables(query, address, both = {
            val intent = Intent()
            intent.putExtra("query", it.first)
            intent.putExtra("address", it.second)
            setResult(Activity.RESULT_OK, intent)
        })
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        (menu?.findItem(R.id.search)?.actionView as? SearchView)?.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    val loading = AtomicBoolean(false)
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!loading.getAndSet(true)) {
                            loadingProgress.show()
                            findAddress(query).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                    {
                                        loading.set(false)
                                        addressSelection(query, it)
                                    },
                                    {
                                        loading.set(false)
                                        showError()
                                    },
                                    {
                                        loading.set(false)
                                        showError()
                                    }
                            )
                        }

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                }
        )
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

    private fun showError() {
        loadingProgress.hide()
        AlertDialog.Builder(this)
                .setMessage(com.mxmariner.tides.R.string.whoops)
                .show()
    }

    private fun findAddress(query: String?): Maybe<Address> {
        loadingProgress.show()
        return Maybe.create<Address> { emitter ->
            try {
                geocoder.getFromLocationName(query, 1).firstOrNull()
            } catch (e: IOException) {
                Log.e(TAG, "", e)
                null
            }?.let {
                emitter.onSuccess(it)
            } ?: {
                emitter.onComplete()
            }()
        }.subscribeOn(Schedulers.io())
    }
}
