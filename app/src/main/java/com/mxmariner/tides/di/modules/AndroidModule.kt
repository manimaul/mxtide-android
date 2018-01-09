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
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.di.scopes.FragmentScope
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

/**
 * [Singleton] scope Android dependencies
 */
@Module
class AndroidModule {

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
    fun provideContext(application: Application): Context = application.applicationContext
}

/**
 * [ActivityScope] scope Android dependencies
 */
@Module
internal class ActivityAndroidModule {

    @ActivityScope
    @Provides
    fun provideLayoutInflater(activity: Activity) : LayoutInflater {
        return activity.layoutInflater
    }

    @ActivityScope
    @Provides
    fun provideFragmentManager(activity: Activity): FragmentManager {
        return activity.fragmentManager
    }

    @ActivityScope
    @Provides
    fun provideSupportFragmentManager(appCompatActivity: AppCompatActivity): android.support.v4.app.FragmentManager {
        return appCompatActivity.supportFragmentManager
    }
}

/**
 * [FragmentScope] scope Android dependencies
 */
@Module
internal class FragmentAndroidModule {

    @FragmentScope
    @Provides
    fun provideLayoutInflater(fragment: Fragment) : LayoutInflater {
        return fragment.layoutInflater
    }

    @Named("childFragmentManager")
    @FragmentScope
    @Provides
    fun provideChildFragmentManager(fragment: Fragment) : android.support.v4.app.FragmentManager{
        return fragment.childFragmentManager
    }
}