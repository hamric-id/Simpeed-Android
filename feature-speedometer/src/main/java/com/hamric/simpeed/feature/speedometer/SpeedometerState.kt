package com.hamric.simpeed.feature.speedometer

data class SpeedometerState(
    val speed: Float = 0f,
    val maxSpeed: Float = 350f,
    val unit: String = "km/h"
)