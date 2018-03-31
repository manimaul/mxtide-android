package com.mxmariner.tides.util

import android.content.SharedPreferences
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxSharedPrefs(kodein: Kodein) : SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedPreferences: SharedPreferences = kodein.instance()
    private val subject = PublishSubject.create<OptionalSignal>()
    private val observingKeys = mutableMapOf<String, ObservingKeyCounter>()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        key?.let {
            observingKeys[it]?.let {
                if (it.clazz == String::class.java) {
                    sharedPreferences?.getString(key, null)?.let {
                        subject.onNext(OptionalSignal(key, it))
                    }
                }
                if (it.clazz == Int::class.java) {
                    sharedPreferences?.getInt(key, 0)?.let {
                        subject.onNext(OptionalSignal(key, it))
                    }
                }
                if (it.clazz == Long::class.java) {
                    sharedPreferences?.getLong(key, 0)?.let {
                        subject.onNext(OptionalSignal(key, it))
                    }
                }
                if (it.clazz == Float::class.java) {
                    sharedPreferences?.getFloat(key, 0.0F)?.let {
                        subject.onNext(OptionalSignal(key, it))
                    }
                }
            }
        }
    }

    fun observeKey(forKey: String, clazz: Class<*>) {
        observingKeys[forKey]?.let {
            it.count++
        } ?: {
           observingKeys[forKey] = ObservingKeyCounter(clazz = clazz)
        }()
    }

    fun unObserveKey(forKey: String) {
        val remove = observingKeys[forKey]?.let {
            it.count -= 1
            it.count < 1
        } ?: false
        if (remove) {
            observingKeys.remove(forKey)
        }
    }

    fun observableKey(forKey: String, clazz: Class<*>): Observable<Any> {
        return subject.filter { it.key == forKey && clazz.isInstance(it.value) }
                .map { it.value!! }
    }

    inline fun <reified T> observeChanges(forKey: String): Observable<T> {
        observeKey(forKey, T::class.java)
        return observableKey(forKey, T::class.java)
                .map {
                    it as T
                }
                .doOnDispose {
                    unObserveKey(forKey)
                }
    }
}

private class ObservingKeyCounter(var count: Int = 1, val clazz: Class<*>)
private class OptionalSignal(val key: String, val value: Any? = null)
