package com.mxmariner.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.mxmariner.globe.activity.GlobeActivity
import com.mxmariner.globe.fragment.GlobeFragment
import com.mxmariner.main.activity.LocationSearchActivity
import com.mxmariner.main.activity.MainActivity
import com.mxmariner.station.StationActivity
import com.mxmariner.tides.application.MxTidesApplication
import com.mxmariner.tides.fragment.SettingsFragment
import com.mxmariner.tides.fragment.TidesFragment
import dagger.Component
import dagger.Subcomponent

object Injector : Application.ActivityLifecycleCallbacks {

  private var internalAppComponent: AppComponent? = null
  val appComponent: AppComponent
    get() = internalAppComponent!!

  private val scopeComponentStack = ScopeComponentStack<Activity, ActivityComponent>()

  fun appInjector(app: MxTidesApplication): AppComponent {
    if (internalAppComponent == null) {
      internalAppComponent = DaggerAppComponent.builder()
        .withModule(ApplicationAndroidModule(app))
        .build()
      app.registerActivityLifecycleCallbacks(this)
    }
    return appComponent
  }

  fun activityInjector(activity: FragmentActivity): ActivityComponent {
    return scopeComponentStack.getComponent(activity)
      ?: createInjector(activity)
  }

  val topActivityInjector: ActivityComponent?
    get() = scopeComponentStack.top

  private fun createInjector(activity: FragmentActivity): ActivityComponent {
    return scopeComponentStack.createComponentForScope(activity) {
      appComponent.plus(ActivityAndroidModule(activity), ActivityBaseModule())
    }
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    (activity as? FragmentActivity)?.let { createInjector(it) }
  }

  override fun onActivityStarted(activity: Activity) = scopeComponentStack.setScopeTop(activity)
  override fun onActivityResumed(activity: Activity) = scopeComponentStack.setScopeTop(activity)
  override fun onActivityPaused(activity: Activity) {}
  override fun onActivityStopped(activity: Activity) {}
  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
  override fun onActivityDestroyed(activity: Activity) =
    scopeComponentStack.releaseComponent(activity)
}

@AppScope
@Component(modules = [ApplicationAndroidModule::class, BaseModule::class])
interface AppComponent {
  fun plus(module1: ActivityAndroidModule, module2: ActivityBaseModule): ActivityComponent
  fun inject(application: MxTidesApplication)

  @Component.Builder
  interface Builder {
    fun withModule(module: ApplicationAndroidModule): Builder
    fun build(): AppComponent
  }
}

@ActivityScope
@Subcomponent(modules = [ActivityAndroidModule::class, ActivityBaseModule::class])
interface ActivityComponent {
  fun inject(activity: SettingsFragment)
  fun inject(activity: TidesFragment)
  fun inject(activity: MainActivity)
  fun inject(activity: LocationSearchActivity)
  fun inject(activity: GlobeActivity)
  fun inject(fragment: GlobeFragment)
  fun inject(activity: StationActivity)
}
