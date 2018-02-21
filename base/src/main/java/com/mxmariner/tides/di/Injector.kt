package com.mxmariner.tides.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.github.salomonbrys.kodein.Kodein
import com.mxmariner.tides.di.modules.ActivityAndroidModule
import com.mxmariner.tides.di.modules.AndroidModule
import com.mxmariner.tides.di.modules.BaseModule


object Injector : Application.ActivityLifecycleCallbacks {

    private lateinit var singletonAssembly: Kodein
    private val activityAssemblies = mutableMapOf<Int, Kodein>()
    private val moduleMixIns = mutableListOf<Kodein.Module>()
    private var foreGroundActivityId = 0

    val applicationAssembly: Kodein
        get() = singletonAssembly

    fun appScopeAssembly(application: Application): Kodein {
        singletonAssembly = Kodein {
            import(AndroidModule(application).module)
            import(BaseModule().module)
        }
        return singletonAssembly
    }

    fun activityScopeAssembly(activity: FragmentActivity): Kodein {
        val id = System.identityHashCode(activity)
        return activityAssemblies[id] ?: {
            val assembly = Kodein {
                extend(singletonAssembly)
                import(ActivityAndroidModule(activity).module)
                moduleMixIns.forEach { import(it) }
            }
            activityAssemblies[id] = assembly
            assembly
        }()
    }

    /**
     * Mix in [Kodein.Module]s to the activity scope from other project module's
     * @param module the modules to mixin
     */
    fun mixInActivityScope(vararg module: Kodein.Module) {
        moduleMixIns.addAll(module)
    }

    //region Application.ActivityLifecycleCallbacks

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        foreGroundActivityId = System.identityHashCode(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        foreGroundActivityId = System.identityHashCode(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        foreGroundActivityId = System.identityHashCode(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivityDestroyed(activity: Activity) {
        val id = System.identityHashCode(activity)
        activityAssemblies.remove(id)
    }

    //endregion

}

