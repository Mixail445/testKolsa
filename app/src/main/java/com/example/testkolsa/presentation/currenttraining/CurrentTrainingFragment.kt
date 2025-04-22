package com.example.testkolsa.presentation.currenttraining

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.testkolsa.databinding.FragmentCurrentTrainingBinding
import com.example.testkolsa.presentation.common.Router
import com.example.testkolsa.presentation.common.launchAndRepeatWithViewLifecycle
import com.example.testkolsa.presentation.common.subscribe
import com.example.testkolsa.presentation.currenttraining.TrainingVideoView.Model
import com.example.testkolsa.presentation.currenttraining.TrainingVideoView.UiLabel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrentTrainingFragment : Fragment() {
    private var _binding: FragmentCurrentTrainingBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private var seekBarJob: Job? = null

    @Inject
    @Named("Host")
    lateinit var router: Router

    @Inject
    lateinit var factory: CurrentTrainingViewModel.Factory

    private val viewModel: CurrentTrainingViewModel by viewModels {
        CurrentTrainingViewModel.LambdaFactory(this) { handle: SavedStateHandle ->
            factory.build(arguments?.getLong("id") ?: 0, handle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCurrentTrainingBinding.inflate(inflater, container, false)
        initViewModel()

        initView()
        return binding.root
    }

    private fun initView() {
        binding.backButton.setOnClickListener {
            viewModel.onEvent(TrainingVideoView.Event.OnClickBack)
        }

        binding.playButton.setOnClickListener {
            viewModel.onEvent(TrainingVideoView.Event.OnClickStartStopButton)
        }

        binding.progressSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = mediaPlayer?.duration ?: 0
                    viewModel.onEvent(TrainingVideoView.Event.UpdatePositionVideo(progress * duration / 100))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.onUserSeekStart()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val duration = mediaPlayer?.duration ?: 0
                val seekPositionPercentage = binding.progressSeekBar.progress
                val seekPositionMs = (seekPositionPercentage * duration) / 100
                viewModel.onUserSeekStop(seekPositionPercentage, duration)
                mediaPlayer?.seekTo(seekPositionMs)
            }
        })
    }

    private fun initViewModel() {
        with(viewModel) {
            subscribe(uiLabels, ::handleUiLabel)
            launchAndRepeatWithViewLifecycle { uiState.collect(::handleState) }
        }
    }

    private fun handleUiLabel(uiLabel: UiLabel) {
        when (uiLabel) {
            is UiLabel.ShowBackScreen -> router.back()
        }
    }

    private fun handleState(model: Model) {
        binding.text.text = model.description
        binding.loadingProgressBar.visibility =
            if (model.isShowProgressBar) View.VISIBLE else View.GONE

        if (model.url.isNotBlank()) {
            playVideoWithMediaPlayer(binding.surfaceView, model.url)
        }

        if (model.isStartStopVideo == false && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        } else if (model.isStartStopVideo == true && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    private fun playVideoWithMediaPlayer(surfaceView: SurfaceView, url: String) {
        if (url.isEmpty()) return
        if (viewModel.uiState.value.url == url && mediaPlayer != null) {
            return
        }
        try {
            releaseMediaPlayer()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)

                setDisplay(surfaceView.holder)
                setOnPreparedListener { player ->
                    player.start()
//                    seekTo(currentPlaybackPosition)
                    updateSeekBarProgress()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e("MediaPlayerError", "Error: $what, $extra")
                    false
                }

                setOnCompletionListener {
                    viewModel.onEvent(TrainingVideoView.Event.UpdatePositionVideo(0))
                    binding.progressSeekBar.progress = 0
                }
            }
            mediaPlayer?.prepareAsync()


        } catch (e: Exception) {
            Log.e("MediaPlayerException", "${e.message}")
        }
    }

    private fun updateSeekBarProgress() {
        seekBarJob?.cancel()
        seekBarJob = lifecycleScope.launch(Dispatchers.Main) {
            //todo
            while (true) {
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                val duration = mediaPlayer?.duration ?: 0
                if (duration > 0) {
                    binding.progressSeekBar.progress = (currentPosition * 100 / duration)
                }
                delay(100)
            }
        }
    }


    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        seekBarJob?.cancel()
    }

    private fun safePauseMediaPlayer() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    override fun onStart() {
        super.onStart()
        router.init(this)
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        safePauseMediaPlayer()
    }

    override fun onStop() {
        super.onStop()
        releaseMediaPlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseMediaPlayer()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }
}
