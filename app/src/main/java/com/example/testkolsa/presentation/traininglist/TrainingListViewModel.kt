package com.example.testkolsa.presentation.traininglist

import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.testkolsa.domain.TrainingRepository
import com.example.testkolsa.presentation.common.Screens
import com.example.testkolsa.presentation.common.SingleLiveData
import com.example.testkolsa.presentation.traininglist.TrainingView.Event
import com.example.testkolsa.presentation.traininglist.TrainingView.Model
import com.example.testkolsa.presentation.traininglist.TrainingView.UiLabel
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

private const val STATE_KEY_TRAINING_SCREEN = "fragment_training"


class TrainingListViewModel @AssistedInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val repository: TrainingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        state.get<Model>(STATE_KEY_TRAINING_SCREEN) ?: Model(
            isLoading = false,
            itemMainRc = emptyList<TrainingUi>()
        )
    )
    val uiState: StateFlow<Model> = _uiState.asStateFlow()
    private val _uiLabels = SingleLiveData<UiLabel>()
    val uiLabels: LiveData<UiLabel> get() = _uiLabels

    init {
        viewModelScope.launch {
            requestTraining()
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnClickMain -> showTScreen(event.id)
            Event.RefreshRc -> refreshRc()
            is Event.OnQueryReviewsTextUpdated -> searchByTitle(event.value)
            is Event.FilterByType -> filterByType(event.value)
        }
    }

    private fun refreshRc() {
        viewModelScope.launch {
            requestTraining()
        }
    }

    private suspend fun requestTraining() {
        if (uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }

        repository.getTrainingRemote()
            .onSuccess { trainingList ->
                val uiTrainingList = trainingList.map { it.mapToUi() }
                _uiState.emit(
                    Model(
                        isLoading = false,
                        itemMainRc = uiTrainingList,
                        originalItems = uiTrainingList
                    )
                )
            }
            .onError { error: Throwable -> processError(error) }
            .also { _uiState.update { it.copy(isLoading = false) } }
    }

    private fun processError(throwable: Throwable) {
        //sample error handler
    }

    private fun showTScreen(id: Long) {
        _uiLabels.value = UiLabel.ShowDetailScreen(Screens.TrainingDetail(id), id)
    }


    fun searchByTitle(query: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    query = query,
                    itemMainRc = currentState.originalItems.filter {
                        it.title.contains(query, ignoreCase = true)
                    }
                )
            }
        }
    }

    fun filterByType(typeId: Long?) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    selectedTypeId = typeId,
                    itemMainRc = if (typeId == null) {
                        currentState.originalItems
                    } else {
                        currentState.originalItems.filter { it.type == typeId }.also {
                        }
                    }
                )
            }
        }
    }


    @AssistedFactory
    interface Factory {
        fun build(
            @Assisted state: SavedStateHandle,
        ): TrainingListViewModel
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

