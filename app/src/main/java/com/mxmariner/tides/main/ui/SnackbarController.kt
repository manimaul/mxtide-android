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
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@ActivityScope
class SnackbarController @Inject constructor(
        private val activity: Activity
) {

    private val rootView by lazy {
        activity.findViewById<View>(R.id.coordinatorLayout)
    }

    fun <T> showRetryIf(default: T, predicate: (T) -> Boolean) : SingleTransformer<T, T> {
        return Transformer(predicate, this, default)
    }

    fun showTryAgain(message: String = activity.getString(R.string.try_again),
                     throwable: Throwable? = null
                     ): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE)
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

private class Transformer<T>(
        private val predicate: (T) -> Boolean,
        private val snackbarController: SnackbarController,
        private val default: T
) : SingleTransformer<T, T> {
    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.flatMap {
            if (predicate(it)) {
                Single.error<T>(Throwable())
            } else {
                Single.just(it)
            }
        }
        .retryWhen {
            it.flatMap {
                snackbarController.showTryAgain(throwable = it).toFlowable()
            }
        }
        .onErrorReturnItem(default)
    }

}
