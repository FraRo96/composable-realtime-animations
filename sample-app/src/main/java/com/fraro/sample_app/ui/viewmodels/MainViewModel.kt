package com.fraro.sample_app.ui.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fraro.composable_realtime_animations.data.models.ParticleVisualizationModel
import com.fraro.composable_realtime_animations.data.models.ScreenPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private fun randomOffset(): Offset {
        val x = Random.nextFloat() * 1080  // assuming a screen width of 1080 pixels
        val y = Random.nextFloat() * 1920  // assuming a screen height of 1920 pixels
        return Offset(x, y)
    }

    private fun randomHeading(): Float {
        return Random.nextFloat() * 360  // heading between 0 and 360 degrees
    }

    fun particleFlow(): Flow<ParticleVisualizationModel> = flow {
        // Create 100 initial particles
        val particles = (1..100).map { id ->
            ParticleVisualizationModel(
                id = id.toLong(),
                screenPosition = ScreenPosition(randomOffset(), randomHeading()),
                duration = 0 // Initial duration can be set to 0
            )
        }.associateBy { it.id }.toMutableMap() // Convert to a mutable map for easy updates

        // Emit the initial particles
        particles.values.forEach { emit(it) }

        while (true) {
            // Randomly choose one of the existing particles to update
            val id = particles.keys.random()
            val newOffset = randomOffset()
            val newHeading = randomHeading()

            // Random duration for the particle movement
            val duration = Random.nextInt(500, 2000)

            // Create the updated particle
            val newParticle = ParticleVisualizationModel(
                id = id,
                screenPosition = ScreenPosition(newOffset, newHeading),
                duration = duration
            )
            particles[id] = newParticle // Update the existing particle
            emit(newParticle) // Emit the updated particle
            delay(50)
        }
    }
}
