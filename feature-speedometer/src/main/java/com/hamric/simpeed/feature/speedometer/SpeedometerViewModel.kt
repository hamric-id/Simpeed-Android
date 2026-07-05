package com.hamric.simpeed.feature.speedometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpeedometerViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SpeedometerState())
    val state: StateFlow<SpeedometerState> = _state.asStateFlow()

    fun updateSpeed(speed: Float) {
        _state.update { it.copy(speed = speed.coerceIn(0f, it.maxSpeed)) }
    }

    fun resetSpeed() {
        _state.update { it.copy(speed = 0f) }
    }
}