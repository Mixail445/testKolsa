package com.example.testkolsa.presentation.currenttraining

import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.testkolsa.Constants.BASE_URL
import com.example.testkolsa.domain.TrainingRepository
import com.example.testkolsa.presentation.common.Screens
import com.example.testkolsa.presentation.common.SingleLiveData
import com.example.testkolsa.presentation.currenttraining.TrainingVideoView.Event
import com.example.testkolsa.presentation.currenttraining.TrainingVideoView.Model
import com.example.testkolsa.presentation.currenttraining.TrainingVideoView.UiLabel
import com.example.testkolsa.presentation.traininglist.TrainingView
import com.example.testkolsa.utils.onError
import com.example.testkolsa.utils.onSuccess
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


class CurrentTrainingViewModel @AssistedInject constructor(
    val repository: TrainingRepository,
    @Assisted private val id: Long,
    @Assisted private val state: SavedStateHandle,
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(
        Model()
    )

    val uiState: StateFlow<Model> = _uiState.asStateFlow()
    private val _uiLabels = SingleLiveData<UiLabel>()
    val uiLabels: LiveData<UiLabel> get() = _uiLabels

    init {
        loadVideoUrl()
        loadTrainingDescription()
    }

    private fun loadVideoUrl() {
        viewModelScope.launch {
            _uiState.update { it.copy(isShowProgressBar = true) }
            repository.getTrainingVideoRemote(id.toInt()).onSuccess { link ->
                val fullUrl = "$BASE_URL${link.link}"
                _uiState.update { it.copy(url = fullUrl, isShowProgressBar = false) }
            }.onError {
                _uiState.update { it.copy(isShowProgressBar = false) }
            }
        }
    }

    private fun loadTrainingDescription() {
        viewModelScope.launch {
            repository.getTrainingRemote().onSuccess { listTraining ->
                val description = listTraining.find { it.id == id }?.description ?: ""
                _uiState.update { it.copy(description = description) }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.OnClickBack -> showBackScreen()
            Event.OnClickStartStopButton -> handlerClickStartStop()
            is Event.UpdatePositionVideo -> updatePositionVideo(event.position)
        }
    }

    private fun updatePositionVideo(position: Int) {
        _uiState.update {
            it.copy(currentTimeVideo = position)
        }
    }

    private fun handlerClickStartStop() {
        _uiState.update {
            it.copy(isStartStopVideo = !it.isStartStopVideo)
        }
    }

    private fun showBackScreen() {
        _uiLabels.value = UiLabel.ShowBackScreen
    }

    fun onUserSeekStart() {
        _uiState.update { it.copy(isUserSeeking = true) }
    }

    fun onUserSeekStop(seekPositionPercentage: Int, duration: Int) {
        val seekPositionMs = (seekPositionPercentage * duration) / 100
        _uiState.update {
            it.copy(
                currentTimeVideo = seekPositionMs,
                isUserSeeking = false,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun build(
            @Assisted id: Long,
            @Assisted state: SavedStateHandle,
        ): CurrentTrainingViewModel
    }

    class LambdaFactory<T : ViewModel>(
        savedStateRegistryOwner: SavedStateRegistryOwner,
        private val create: (handle: SavedStateHandle) -> T,
    ) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle,
        ): T = create(handle) as T
    }

}
