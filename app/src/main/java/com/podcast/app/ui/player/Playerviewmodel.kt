package com.podcast.app.ui.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcast.app.core.services.MediaPlaybackService
import javax.inject.Inject

private const val DEMO_URL =
    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
private const val DEMO_TITLE  = "السفر الصامت وضوضاء الإعلانات"
private const val DEMO_ART    =
    "https://media.npr.org/assets/img/2023/03/01/npr-news-now_square.png"



data class PlayerState(
    val isVisible: Boolean = true,
    val isPlaying: Boolean = false,
    val title: String = DEMO_TITLE,
    val podcastName: String = "جادي",
    val thumbnailUrl: String = DEMO_ART,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val queueCount: Int = 4,
    val skipSeconds: Int = 15,
    val isBuffering: Boolean = false
) {
    val progress: Float
        get() = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f

    val remainingLabel: String
        get() {
            val remaining = ((durationMs - positionMs) / 1000L).coerceAtLeast(0)
            val m = remaining / 60
            val h = m / 60
            val mm = m % 60
            return if (h > 0) "-${h}:${mm.toString().padStart(2, '0')} د"
            else "-${m} د"
        }
}



@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var positionTickJob: Job? = null

    init {
        connectToService()
    }


    private fun connectToService() {
        val token = SessionToken(
            context,
            ComponentName(context, MediaPlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, token).buildAsync()
        controllerFuture!!.addListener({
            controller = controllerFuture!!.get()
            prepareDemo()
            attachPlayerListener()
        }, MoreExecutors.directExecutor())
    }

    private fun prepareDemo() {
        val ctrl = controller ?: return
        val metadata = MediaMetadata.Builder()
            .setTitle(DEMO_TITLE)
            .setArtworkUri(android.net.Uri.parse(DEMO_ART))
            .build()
        val item = MediaItem.Builder()
            .setUri(DEMO_URL)
            .setMediaMetadata(metadata)
            .build()
        ctrl.setMediaItem(item)
        ctrl.prepare()
    }

    private fun attachPlayerListener() {
        val ctrl = controller ?: return
        ctrl.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
                if (isPlaying) startPositionTick() else stopPositionTick()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val buffering = playbackState == Player.STATE_BUFFERING
                val duration = ctrl.duration.takeIf { it > 0 } ?: 0L
                _state.value = _state.value.copy(
                    isBuffering = buffering,
                    durationMs = duration
                )
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                _state.value = _state.value.copy(
                    title = mediaMetadata.title?.toString() ?: DEMO_TITLE,
                    thumbnailUrl = mediaMetadata.artworkUri?.toString() ?: DEMO_ART
                )
            }
        })
    }


    fun playPause() {
        val ctrl = controller ?: return
        if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
    }

    fun skipForward() {
        val ctrl = controller ?: return
        ctrl.seekTo((ctrl.currentPosition + _state.value.skipSeconds * 1000L)
            .coerceAtMost(ctrl.duration.coerceAtLeast(0L)))
        syncPosition()
    }

    fun seekTo(progress: Float) {
        val ctrl = controller ?: return
        val target = (progress * ctrl.duration.coerceAtLeast(0L)).toLong()
        ctrl.seekTo(target)
        _state.value = _state.value.copy(positionMs = target)
    }


    private fun startPositionTick() {
        stopPositionTick()
        positionTickJob = viewModelScope.launch {
            while (true) {
                syncPosition()
                delay(500L)
            }
        }
    }

    private fun stopPositionTick() {
        positionTickJob?.cancel()
        positionTickJob = null
    }

    private fun syncPosition() {
        val ctrl = controller ?: return
        _state.value = _state.value.copy(
            positionMs = ctrl.currentPosition.coerceAtLeast(0L),
            durationMs = ctrl.duration.takeIf { it > 0 } ?: _state.value.durationMs
        )
    }


    override fun onCleared() {
        super.onCleared()
        stopPositionTick()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}