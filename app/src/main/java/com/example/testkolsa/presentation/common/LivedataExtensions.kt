package com.example.testkolsa.presentation.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

fun <T> Fragment.subscribe(liveData: LiveData<T>, onNext: (t: T) -> Unit) {
    liveData.observe(viewLifecycleOwner) {
        if (it != null) {
            onNext(it)
        }
    }
}