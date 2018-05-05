package com.mxmariner.tides.di.modules

import android.animation.ArgbEvaluator
import android.app.Activity
import android.app.Application
import android.app.FragmentManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.github.salomonbrys.kodein.*
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.MXTideFactory
import com.mxmariner.tides.factory.StationPresentationFactory
import com.mxmariner.tides.repository.HarmonicsRepo
import com.mxmariner.tides.routing.Router
import com.mxmariner.tides.settings.Preferences
import com.mxmariner.tides.ui.SnackbarController
import com.mxmariner.tides.ui.UnitFormats
import com.mxmariner.tides.util.*

internal class BaseModule {
    val module = Kodein.Module {
        bind() from singleton { HarmonicsRepo(this) }

        bind<ITidesAndCurrents>() with singleton { MXTideFactory.createTidesAndCurrents() }

        bind() from singleton { Preferences(this) }

        bind() from singleton { RxSharedPrefs(this) }

        bind() from provider { UnitFormats(this) }

        bind() from provider { StationPresentationFactory(this) }
    }
}

internal class AndroidModule(private val application: Application) {

    val module = Kodein.Module {

        bind<Resources>() with provider { application.resources }

        bind<Application>() with provider { application }

        bind<SharedPreferences>() with provider { PreferenceManager.getDefaultSharedPreferences(application) }

        bind<AssetManager>() with provider { application.assets }

        bind<DisplayMetrics>() with provider { instance<Resources>().displayMetrics }

        bind<Context>() with provider { application.applicationContext }

        bind() from provider { ArgbEvaluator() }

        bind() from provider { Handler() }

        bind<Geocoder>() with provider { Geocoder(instance()) }

        bind<ClipboardManager>() with provider {
            application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }

        bind<ConnectivityManager>() with provider {
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        bind<InputMethodManager>() with provider {
            application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        bind<LocationManager>() with provider {
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        bind<NotificationManager>() with provider {
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }


        bind<TelephonyManager>() with provider {
            application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }

        bind<WindowManager>() with provider {
            application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
    }
}

internal class ActivityAndroidModule(private val fragmentActivity: FragmentActivity) {

    val module = Kodein.Module {

        bind() from provider { fragmentActivity }

        bind() from provider { Router(this) }

        bind() from provider { SnackbarController(this) }

        bind<Activity>() with provider { fragmentActivity }

        bind<RxPermission>() with provider { RxPermissionImpl(this) }

        bind<RxActivityResult>() with provider { RxActivityResultImpl(this) }

        bind<RxLocation>() with provider { RxLocationImpl(this) }

        bind<LayoutInflater>() with provider { fragmentActivity.layoutInflater }

        bind<FragmentManager>() with provider { fragmentActivity.fragmentManager }

        bind<android.support.v4.app.FragmentManager>() with provider { fragmentActivity.supportFragmentManager }
    }
}
