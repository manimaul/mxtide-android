package com.mxmariner.tides.main.ui

import android.app.Activity
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.view.View
import com.mxmariner.tides.R
import com.mxmariner.tides.di.scopes.ActivityScope
import com.mxmariner.tides.main.extensions.safeComplete
import com.mxmariner.tides.main.extensions.safeError
import com.mxmariner.tides.main.extensions.safeSuccess
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@ActivityScope
class SnackbarController @Inject constructor(
        private val activity: Activity
) {

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
            val msg = message ?: activity.getString(R.string.whoops)
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
