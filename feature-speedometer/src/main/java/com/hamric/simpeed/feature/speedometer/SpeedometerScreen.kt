package com.hamric.simpeed.feature.speedometer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hamric.simpeed.common.ui.theme.SimpeedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedometerScreen(
    viewModel: SpeedometerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SimpeedTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Speedometer",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SpeedGauge(
                    speed = state.speed,
                    maxSpeed = state.maxSpeed,
                    modifier = Modifier.size(250.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "${state.speed.toInt()} ${state.unit}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Adjust Speed",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Slider(
                    value = state.speed,
                    onValueChange = viewModel::updateSpeed,
                    valueRange = 0f..state.maxSpeed,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = viewModel::resetSpeed,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun SpeedGauge(
    speed: Float,
    maxSpeed: Float,
    modifier: Modifier = Modifier
) {
    val percentage = (speed / maxSpeed).coerceIn(0f, 1f)
    val color = when {
        percentage < 0.33 -> Color.Green
        percentage < 0.66 -> Color.Yellow
        else -> Color.Red
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.foundation.Canvas(
            modifier = modifier
        ) {
            val strokeWidth = size.width * 0.1f
            val radius = size.width / 2 - strokeWidth / 2

            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = strokeWidth / 2,
                    y = strokeWidth / 2
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = radius * 2,
                    height = radius * 2
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )

            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * percentage.toFloat(),
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = strokeWidth / 2,
                    y = strokeWidth / 2
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = radius * 2,
                    height = radius * 2
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }

        androidx.compose.foundation.layout.Box(
            modifier = modifier
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = "${speed.toInt()}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "km/h",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}