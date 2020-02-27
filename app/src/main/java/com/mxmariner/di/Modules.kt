package com.mxmariner.di

import android.animation.ArgbEvaluator
import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.LocationManager
import android.os.Handler
import android.preference.PreferenceManager
import android.telecom.TelecomManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.MXTideFactory
import com.mxmariner.tides.application.MxTidesApplication
import com.mxmariner.tides.util.RxActivityResult
import com.mxmariner.tides.util.RxActivityResultImpl
import com.mxmariner.tides.util.RxLocation
import com.mxmariner.tides.util.RxLocationImpl
import com.mxmariner.tides.util.RxPermission
import com.mxmariner.tides.util.RxPermissionImpl
import dagger.Module
import dagger.Provides

@Module
class BaseModule {

  @AppScope
  @Provides
  fun tidesAndCurrents(): ITidesAndCurrents = MXTideFactory.createTidesAndCurrents()
}

@Module
class ApplicationAndroidModule(private val application: MxTidesApplication) {

  @Provides
  fun appResources(): Resources = application.resources

  @Provides
  fun mxApplication(): MxTidesApplication = application

  @Provides
  fun application(): Application = application

  @Provides
  fun sharedPrefs(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

  @Provides
  fun assetManager(): AssetManager = application.assets

  @Provides
  fun displayMetrics(res: Resources): DisplayMetrics = res.displayMetrics

  @Provides
  fun appContext(): Context = application.applicationContext

  @Provides
  fun argbEvaluator(): ArgbEvaluator = ArgbEvaluator()

  @Provides
  fun handler(): Handler = Handler()

  @Provides
  fun geoCoder(ctx: Context): Geocoder = Geocoder(ctx)

  @Provides
  fun clipboardManager(application: MxTidesApplication): ClipboardManager =
    application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

  @Provides
  fun inputMethodManager(application: MxTidesApplication): InputMethodManager =
    application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

  @Provides
  fun locationManager(application: MxTidesApplication): LocationManager =
    application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

  @Provides
  fun notificationManager(application: MxTidesApplication): NotificationManager =
    application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  @Provides
  fun telephonyManager(application: MxTidesApplication): TelecomManager =
    application.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

  @Provides
  fun windowManager(application: MxTidesApplication): WindowManager =
    application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}

@Module
class ActivityBaseModule {
  @Provides
  fun rxLocation(impl: RxLocationImpl): RxLocation = impl

  @Provides
  fun rxPermission(impl: RxPermissionImpl): RxPermission = impl

  @Provides
  fun rxActivityResult(impl: RxActivityResultImpl): RxActivityResult = impl
}

@Module
class ActivityAndroidModule(
  private val fragmentActivity: FragmentActivity
) {

  @Provides
  fun fragmentActivity(): FragmentActivity = fragmentActivity

  @Provides
  fun activity(): Activity = fragmentActivity

  @Provides
  fun layoutInflater(): LayoutInflater = fragmentActivity.layoutInflater

  @Provides
  fun fragmentManager(): FragmentManager = fragmentActivity.supportFragmentManager
}
