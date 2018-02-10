package com.mxmariner.tides.extensions

import android.util.Log
import io.reactivex.*

fun <T> ObservableEmitter<T>.safeComplete() {
    if (!this.isDisposed) {
        this.onComplete()
    }
}

fun <T> ObservableEmitter<T>.safeError(error: Throwable) {
    if (!this.isDisposed) {
        this.onError(error)
    }
}

fun <T> ObservableEmitter<T>.safeNext(next: T) {
    if (!this.isDisposed) {
        this.onNext(next)
    }
}

fun <T> MaybeEmitter<T>.safeComplete() {
    if (!this.isDisposed) {
        this.onComplete()
    }
}

fun <T> MaybeEmitter<T>.safeError(error: Throwable) {
    if (!this.isDisposed) {
        this.onError(error)
    }
}

fun <T> MaybeEmitter<T>.safeSuccess(success: T) {
    if (!this.isDisposed) {
        this.onSuccess(success)
    }
}

fun <T> Observable<T>.debug(tag: String): Observable<T> = this.compose(DebugObservableTransform(tag))

fun Completable.debug(tag: String): Completable = this.compose(DebugCompletableTransform(tag))

fun <T> Single<T>.debug(tag: String): Single<T> = this.compose(DebugSingleTransform(tag))

fun <T> Maybe<T>.debug(tag: String): Maybe<T> = this.compose(DebugMaybeTransform(tag))

object DebugTransform {

    @JvmStatic fun <T> debugObservable(tag: String): ObservableTransformer<T, T> = DebugObservableTransform(tag)

    @JvmStatic fun debugCompletable(tag: String): CompletableTransformer = DebugCompletableTransform(tag)

    @JvmStatic fun <T> debugSingle(tag: String): SingleTransformer<T, T> = DebugSingleTransform(tag)

    @JvmStatic fun <T> debugMaybe(tag: String): MaybeTransformer<T, T> = DebugMaybeTransform(tag)
}

private class DebugObservableTransform<T>(val tag: String) : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.doOnSubscribe {
            Log.d(tag, "onSubscribe")
        }.doOnNext {
            Log.d(tag, "onNext($it)")
        }.doOnError {
            Log.d(tag, "onError($it)")
        }.doOnComplete {
            Log.d(tag, "onComplete")
        }.doOnDispose {
            Log.d(tag, "onDispose")
        }
    }
}

private class DebugSingleTransform<T>(val tag: String) : SingleTransformer<T, T> {
    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.doOnSubscribe {
            Log.d(tag, "onSubscribe")
        }.doOnError {
            Log.d(tag, "onError($it)")
        }.doOnSuccess {
            Log.d(tag, "onSuccess($it)")
        }.doOnDispose {
            Log.d(tag, "onDispose")
        }
    }
}

private class DebugMaybeTransform<T>(val tag: String) : MaybeTransformer<T, T> {
    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.doOnSubscribe {
            Log.d(tag, "onSubscribe")
        }.doOnError {
            Log.d(tag, "onError($it)")
        }.doOnSuccess {
            Log.d(tag, "onSuccess($it)")
        }.doOnDispose {
            Log.d(tag, "onDispose")
        }
    }
}

private class DebugCompletableTransform(val tag: String) : CompletableTransformer {
    override fun apply(upstream: Completable): CompletableSource {
        return upstream.doOnSubscribe {
            Log.d(tag, "onSubscribe")
        }.doOnError {
            Log.d(tag, "onError($it)")
        }.doOnComplete {
            Log.d(tag, "onComplete")
        }.doOnDispose {
            Log.d(tag, "onDispose")
        }
    }
}
