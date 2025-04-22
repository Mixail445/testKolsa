package com.example.testkolsa.presentation.common


sealed class Screens {
    data object Training : Screens()

    data class TrainingDetail(val id: Long) : Screens()
}