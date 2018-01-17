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
import com.mxmariner.tides.main.util.Variable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableSource
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

    fun <T> showRetryIf(predicate: (T) -> Boolean): ObservableTransformer<T, T> {
        return RetryTransformer(predicate, this)
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

private class RetryTransformer<T>(
        private val predicate: (T) -> Boolean,
        private val snackbarController: SnackbarController
) : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        val capture = Variable<T>()
        return upstream.flatMap {
            if (predicate(it)) {
                capture.value = it
                Observable.error<T>(Throwable())
            } else {
                Observable.just(it)
            }
        }.retryWhen { errorObservable ->
            errorObservable.flatMap { error ->
                snackbarController.showTryAgain(throwable = error).toObservable()
            }
        }.onErrorResumeNext(capture.observable.take(1))
    }
}
