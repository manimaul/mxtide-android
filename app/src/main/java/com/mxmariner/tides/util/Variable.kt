package com.mxmariner.tides.util

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject

class Variable<T> : Consumer<T> {

    private val behaviorSubject = BehaviorSubject.create<T>()

    constructor()

    constructor(value: T?) {
        this.value = value
    }

    var value: T? = null
        set(newValue) {
            newValue?.let {
                behaviorSubject.onNext(it)
            }
            field = newValue
        }

    val observable: Observable<T>
        get() = behaviorSubject.hide()

    override fun accept(t: T) {
        value = t
    }
}
