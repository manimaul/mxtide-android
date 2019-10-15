package com.mxmariner.tides.ui

import android.content.res.Resources
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.tides.R
import com.mxmariner.tides.extensions.safeComplete
import com.mxmariner.tides.extensions.safeError
import com.mxmariner.tides.extensions.safeSuccess
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers

class SnackbarController(kodein: Kodein) {

    private val resources: Resources = kodein.instance()
    private val activity: FragmentActivity = kodein.instance()
    private val rootView by lazy {
        activity.findViewById<View>(R.id.coordinatorLayout)
    }

    /**
     * Retry when a the user chooses to retry via snackbar until the Downstream signal type is encountered.
     * @param Upstream the input sequence type
     * @param Downstream the downstream sequence type
     */
    inline fun <Upstream, reified Downstream : Upstream> retryWhenSnackbarUntilType(): ObservableTransformer<Upstream, Downstream> {
        return ObservableTransformer {
            it.flatMap {
                if (it is Downstream) {
                    Observable.just<Downstream>(it)
                } else {
                    Observable.error<Downstream>(Throwable())
                }.retryWhen { errorObservable ->
                    errorObservable.flatMap { error ->
                        showTryAgain(throwable = error).toObservable()
                    }
                }
            }
        }
    }

    fun showTryAgain(message: String? = null,
                     throwable: Throwable? = null
    ): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            val msg = message ?: resources.getString(R.string.whoops)
            Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.try_again) {
                        emitter.safeSuccess(Any())
                    }
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            throwable?.let {
                                emitter.safeError(it)
                            } ?: {
                                emitter.safeComplete()
                            }()
                        }
                    })
                    .show()
        }.subscribeOn(AndroidSchedulers.mainThread())
    }
}
