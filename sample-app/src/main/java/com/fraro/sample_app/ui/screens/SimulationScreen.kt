package com.fraro.sample_app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fraro.composable_realtime_animations.data.models.ParticleVisualizationModel
import com.fraro.composable_realtime_animations.ui.screens.RealtimeAnimationCanvas
import com.fraro.sample_app.data.CalibrationPoint
import com.fraro.sample_app.ui.viewmodels.MainViewModel

@Composable
fun SimulationScreen() {

    val context = LocalContext.current
    val lifecycleOwner = context as ViewModelStoreOwner
    val viewModel: MainViewModel = ViewModelProvider(lifecycleOwner)[MainViewModel::class.java]

    val textMeasurer = rememberTextMeasurer()

    val collectedFlow = viewModel.backwardFlow.collectAsStateWithLifecycle(
        initialValue = null,
        minActiveState = Lifecycle.State.RESUMED
    )
    RealtimeAnimationCanvas(animationFlow = viewModel.backwardFlow, samplingInterval = 100, isForward = false)

    Box {
        Canvas(modifier = Modifier.fillMaxSize()) {
            /*collectedFlow.value?.let {
                drawCircle(
                    color = Color(0xFF66BB6A).copy(alpha = 0.8f), // Default to black if color is null
                    center = it.screenPosition.offset,
                    radius = 20.dp.toPx()
                )
            }*/
            viewModel.trajectories.forEach {
                it.value.forEach { it1 ->
                    drawCalibrationPoint(it1, textMeasurer)
                }
            }
        }
    }
}


private fun DrawScope.drawCalibrationPoint(particle: CalibrationPoint, textMeasurer: TextMeasurer) {
    drawCircle(
        color = Color(0xFF66BB6A).copy(alpha = 0.3f), // Default to black if color is null
        center = particle.screenPosition.offset,
        radius = 10.dp.toPx()
    )
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString("${particle.order}"),
            style = TextStyle(color = Color.Black, fontSize = 10.sp)
        )
    val textSize = textLayoutResult.size

    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(
            x  = particle.screenPosition.offset.x - textSize.width / 2,
            y = particle.screenPosition.offset.y - textSize.height / 2
        )
    )
}