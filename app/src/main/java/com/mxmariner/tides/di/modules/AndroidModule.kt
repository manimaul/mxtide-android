package com.mxmariner.tides.di.modules

import android.animation.ArgbEvaluator
import android.app.Application
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
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import dagger.Module
import dagger.Provides
import java.util.*

@Module
class AndroidModule(private val application: Application) {

    @Provides
    fun provideApplication(): Application = application

    @Provides
    fun provideArgbEvaluator(): ArgbEvaluator = ArgbEvaluator()

    @Provides
    fun provideAssetManager(context: Context): AssetManager = context.assets

    @Provides
    fun provideClipboardManager(context: Context): ClipboardManager = context
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun provideConnectivityManager(context: Context): ConnectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun provideDisplayMetrics(resources: Resources): DisplayMetrics = resources.displayMetrics

    @Provides
    fun provideGeocoder(context: Context): Geocoder = Geocoder(context, Locale.US)

    @Provides
    fun provideHandler(): Handler = Handler()

    @Provides
    fun provideInputMethodManager(context: Context): InputMethodManager = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Provides
    fun provideLocationManager(context: Context): LocationManager = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun provideNotificationManager(context: Context): NotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context)

    @Provides
    fun provideTelephonyManager(context: Context): TelephonyManager = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Provides
    fun provideWindowManager(context: Context): WindowManager = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager

    @Provides
    fun provideContext(): Context = application.applicationContext
}