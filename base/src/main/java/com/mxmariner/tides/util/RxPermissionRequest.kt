package com.mxmariner.tides.util

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v13.app.FragmentCompat
import android.support.v4.content.PermissionChecker
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.SingleSubject
import java.util.concurrent.ThreadLocalRandom

interface RxPermission {

    /**
     * Request runtime permissions and get an Observable<List<[PermissionRequestResult]>> result.
     *
     * @param permissions The permissions to request
     */
    fun requestPermissions(vararg permissions: String): Single<List<PermissionRequestResult>>
}

internal class RxPermissionImpl(kodein: Kodein) : RxPermission {

    private val fragmentManager: FragmentManager = kodein.instance()

    override fun requestPermissions(vararg permissions: String): Single<List<PermissionRequestResult>> {
        return Single.defer {
            val broker = PermissionRequestBroker()
            broker.arguments = Bundle()
            broker.arguments?.putStringArray(PermissionRequestBroker.KEY_PERMISSIONS, permissions)
            broker.arguments?.putInt(PermissionRequestBroker.KEY_REQUEST_CODE, rand16BitInt())
            fragmentManager.beginTransaction()
                    .add(broker, PermissionRequestBroker.TAG)
                    .commit()
            broker.resultObservable
        }.subscribeOn(AndroidSchedulers.mainThread())
    }
}

data class PermissionRequestResult(val permission: String, @PermissionChecker.PermissionResult val grantResult: Int)

/**
 * @return a pseudo-random number between min and max, inclusive.
 */
private const val MIN_16BIT_INT = 0
private const val MAX_16BIT_INT = 65535
fun rand16BitInt(): Int {
    return ThreadLocalRandom.current().nextInt(MIN_16BIT_INT, MAX_16BIT_INT + 1)
}

/**
 * Headless retain fragment for brokering an Observable result from requesting permissions.
 */
class PermissionRequestBroker : Fragment() {
    private val resultSubject = SingleSubject.create<List<PermissionRequestResult>>()
    val resultObservable: Single<List<PermissionRequestResult>>
        get() = resultSubject

    companion object {
        internal val TAG = PermissionRequestBroker::class.java.simpleName
        internal const val KEY_PERMISSIONS = "KEY_PERMISSIONS"
        internal const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
    }

    private val requestCode: Int
        get() = arguments?.getInt(KEY_REQUEST_CODE) ?: 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        arguments?.getStringArray(KEY_PERMISSIONS)?.let {
            FragmentCompat.requestPermissions(this, it, requestCode)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            resultSubject.onSuccess(grantResults.zip(permissions).map { PermissionRequestResult(it.second, it.first) })
            fragmentManager
                    .beginTransaction()
                    .remove(this)
                    .commit()
        }
    }
}
