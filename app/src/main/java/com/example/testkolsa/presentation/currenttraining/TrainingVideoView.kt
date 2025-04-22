package com.example.testkolsa.presentation.currenttraining

class TrainingVideoView {
    data class Model(
        val url: String = "",
        val description: String = "",
        val isShowProgressBar: Boolean = false,
        val progressVideo: Int = 0,
        val isStartStopVideo: Boolean = true,
        val currentTimeVideo: Int = 0,
        val isSeekBarUpdating: Boolean = false,
        val isUserSeeking: Boolean = false
    )

    sealed interface Event {
        data class UpdatePositionVideo(val position: Int) : Event
        data object OnClickBack : Event
        data object OnClickStartStopButton : Event
    }

    sealed interface UiLabel {
        data object ShowBackScreen : UiLabel
    }

}