package com.mxmariner.tides.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import java.lang.ref.WeakReference

object Injector : Application.ActivityLifecycleCallbacks {

    private var activityRef: WeakReference<Activity>? = null

    fun inject(activity: AppCompatActivity) {
        activityRef = WeakReference(activity)
        AndroidInjection.inject(activity)
    }

    val foregroundActivity: AppCompatActivity
        get() = activityRef?.get() as? AppCompatActivity
                ?: throw RuntimeException("Foreground activity does not exist or is not an AppCompatActivity")

    // region spinning up

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activityRef?.get() != activity) {
            activityRef = WeakReference(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activityRef?.get() != activity) {
            activityRef = WeakReference(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (activityRef?.get() != activity) {
            activityRef = WeakReference(activity)
        }
    }

    // endregion

    // region spinning down down

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivityDestroyed(activity: Activity) {
        if (activityRef?.get() == activity) {
            activityRef = null
        }
    }

    // endregion
}

