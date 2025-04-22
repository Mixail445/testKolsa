package com.example.testkolsa.presentation.traininglist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import com.example.testkolsa.R
import com.example.testkolsa.databinding.FragmentTrainingBinding
import com.example.testkolsa.presentation.common.RecyclerViewItemDecoration
import com.example.testkolsa.presentation.common.Router
import com.example.testkolsa.presentation.common.TrainingType
import com.example.testkolsa.presentation.common.launchAndRepeatWithViewLifecycle
import com.example.testkolsa.presentation.common.subscribe
import com.example.testkolsa.presentation.traininglist.TrainingView.Event
import com.example.testkolsa.presentation.traininglist.TrainingView.Model
import com.example.testkolsa.presentation.traininglist.TrainingView.UiLabel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import jakarta.inject.Named

@AndroidEntryPoint
class TrainingListFragment : Fragment() {
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    @Inject
    @Named("Host")
    lateinit var router: Router

    @Inject
    lateinit var factory: TrainingListViewModel.Factory
    private val adapterMain = TrainingScreenAdapter(
        onItemClicked = { id ->
            viewModel.onEvent(Event.OnClickMain(id.toLong()))
        },
    )
    private val viewModel: TrainingListViewModel by viewModels {
        TrainingListViewModel.LambdaFactory(this) { handle: SavedStateHandle ->
            factory.build(handle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        initView()
        initViewModel()
        return binding.root
    }

    private fun initView() {
        binding.rvContent.apply {
            adapter = adapterMain
            setHasFixedSize(true)
            addItemDecoration(RecyclerViewItemDecoration())
        }
        binding.scTraining.setOnRefreshListener {
            viewModel.onEvent(Event.RefreshRc)
        }
        binding.svQuery.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.onEvent(Event.OnQueryReviewsTextUpdated(newText))
                    return true
                }
            },
        )
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedTypeId = if (checkedIds.isEmpty()) {
                null
            } else {
                when (checkedIds.first()) {
                    R.id.chip_cardio -> TrainingType.CARDIO.id
                    R.id.chip_strength -> TrainingType.STRENGTH.id
                    R.id.chip_stretching -> TrainingType.STRETCHING.id
                    R.id.chip_yoga -> TrainingType.YOGA.id
                    R.id.chip_box -> TrainingType.BOX.id
                    else -> null
                }
            }
            viewModel.onEvent(Event.FilterByType(selectedTypeId))
        }
    }

    private fun initViewModel() {
        with(viewModel) {
            subscribe(uiLabels, ::handleUiLabel)
            launchAndRepeatWithViewLifecycle { uiState.collect(::handleState) }
        }
    }

    private fun handleUiLabel(uiLabel: UiLabel): Unit = when (uiLabel) {
        is UiLabel.ShowDetailScreen -> {
            val bundle = bundleOf("id" to uiLabel.id)
            router.navigateTo(uiLabel.screens, bundle = bundle)
        }
    }

    private fun handleState(model: Model) {
        adapterMain.items = model.itemMainRc
        binding.scTraining.isRefreshing = model.isLoading
        binding.svQuery.setQuery(model.query, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        router.init(this)
    }

    override fun onStop() {
        super.onStop()
        router.clear()
    }

}