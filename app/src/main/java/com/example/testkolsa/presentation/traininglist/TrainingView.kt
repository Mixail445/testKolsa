package com.example.testkolsa.presentation.traininglist

import com.example.testkolsa.presentation.common.Screens

class TrainingView {
    data class Model(
        val itemMainRc: List<TrainingUi> = emptyList(),
        val originalItems: List<TrainingUi> = emptyList(),
        val isLoading: Boolean = false,
        val query: String = "",
        val selectedTypeId: Long? = null
    )

    sealed interface Event {
        data class OnClickMain(
            val id: Long,
        ) : Event

        data object RefreshRc : Event
        data class OnQueryReviewsTextUpdated(val value: String) : Event
        data class FilterByType(val value: Long?) : Event
    }

    sealed interface UiLabel {
        data class ShowDetailScreen(
            val screens: Screens,
            val id: Long,
        ) : UiLabel
    }

}