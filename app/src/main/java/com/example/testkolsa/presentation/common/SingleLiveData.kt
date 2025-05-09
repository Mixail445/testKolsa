package com.example.testkolsa.presentation.common

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


class SingleLiveData<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    override fun observe(
        owner: LifecycleOwner,
        observer: Observer<in T>,
    ) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        // Observe the internal MutableLiveData
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        setValue(null)
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}