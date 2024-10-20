package com.fraro.composable_realtime_animations.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fraro.composable_realtime_animations.data.models.ParticleVisualizationModel
import com.fraro.composable_realtime_animations.domain.AnimationDataStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class RealtimeAnimationViewModel @Inject constructor(
    private val animationDataStreamerUseCase: AnimationDataStreamerUseCase
): ViewModel() {
    
    lateinit var animationFlow: StateFlow<ConcurrentHashMap<Long, ParticleVisualizationModel>?>

    @OptIn(FlowPreview::class)
    private fun transformStream(
        samplingRate: Int
    ): Flow<ConcurrentHashMap<Long, ParticleVisualizationModel>> {
        val particleMap = ConcurrentHashMap<Long, ParticleVisualizationModel>()

        return animationDataStreamerUseCase.streamFlow
            .filterNotNull()
            .onEach { particleModel ->
                particleMap[particleModel.id] = particleModel
            }
            .sample(samplingRate.toLong())
            .map {
                val snapshot = ConcurrentHashMap(particleMap)
                particleMap.clear()
                snapshot
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    fun generateStream(
        flow: Flow<ParticleVisualizationModel>,
        samplingRate: Int
    ) {
        animationDataStreamerUseCase.generateStream(flow)

        animationFlow = transformStream(samplingRate)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
    }

    /* suspend fun generateStream(
        bufferedInputStream: BufferedInputStream,
        callback: (BufferedInputStream) -> Flow<ParticleVisualizationModel>
    ) {
        animationDataStreamerUseCase.generateStream(bufferedInputStream, callback)
    }*/


}