package com.mxmariner.main.activity

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.mxmariner.di.Injector
import com.mxmariner.tides.R
import com.mxmariner.tides.extensions.evaluateNullables
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_layout.*
import java.io.IOException
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@Keep
class LocationSearchActivity : AppCompatActivity() {

    companion object {
        val TAG = LocationSearchActivity::class.java.simpleName
    }

    @Inject lateinit var geocoder: Geocoder
    private val compositeDisposable = CompositeDisposable()
    private val loading = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.activityInjector(this).inject(this)

        setContentView(R.layout.activity_search_layout)
        ll.text = HtmlCompat.fromHtml("&quot;${getString(R.string.lat_lng_ex)}&quot;", FROM_HTML_MODE_LEGACY)
        address.text = HtmlCompat.fromHtml("&quot;${getString(R.string.address_ex)}&quot;", FROM_HTML_MODE_LEGACY)
        setSupportActionBar(toolbar)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun addressSelection(address: Address?) {
        address?.let{
            val intent = Intent()
            intent.putExtra("address", it)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        (menu?.findItem(R.id.search)?.actionView as? SearchView)?.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!loading.getAndSet(true)) {
                            loadingProgress.show()
                            query(query)
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

    private fun query(query: String?) {
        compositeDisposable.add(
                findAddress(query).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        {
                            loading.set(false)
                            addressSelection(it)
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
        )
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
                .setMessage(R.string.whoops)
                .show()
    }

    private fun findAddress(query: String?): Maybe<Address> {
        loadingProgress.show()
        return Maybe.create<Address> { emitter ->
            try {
                attemptExtractCoordinates(query) ?: attemptGeoCoderSearch(query)
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

    private fun attemptGeoCoderSearch(query: String?): Address? {
        return geocoder.getFromLocationName(query, 1).firstOrNull()?.apply {
            query?.let {
                featureName = it
            }
        }
    }

    private fun attemptExtractCoordinates(query: String?): Address? {
        return query?.let {
            it.split(",").takeIf {
                it.size == 2
            }?.let {
                val lat = it.firstOrNull()?.toDoubleOrNull()
                val lng = it.lastOrNull()?.toDoubleOrNull()
                evaluateNullables(lat, lng, both = {
                    val address = Address(Locale.getDefault())
                    address.latitude = it.first
                    address.longitude = it.second
                    address.featureName = getString(R.string.coordinates)
                    address
                })
            }
        }
    }
}
