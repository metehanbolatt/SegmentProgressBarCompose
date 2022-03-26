package com.metehanbolat.segmentprogressbarcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.metehanbolat.segmentprogressbarcompose.models.SegmentColors
import com.metehanbolat.segmentprogressbarcompose.models.SegmentCoordinates
import com.metehanbolat.segmentprogressbarcompose.segment.SegmentProgressBar
import com.metehanbolat.segmentprogressbarcompose.ui.theme.Green200
import com.metehanbolat.segmentprogressbarcompose.ui.theme.SegmentProgressBarComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SegmentProgressBarComposeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Segment ProgressBar",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        SegmentProgressBarView()
                    }
                }
            }
        }
    }
}

enum class ProgressState {
    Idle, Progressing
}

@Composable
fun SegmentProgressBarView() {
    val progressAnimatedDuration = 1000
    val breathEffectAnimationDuration = 1800
    val enableBreathEffectAnimation by remember { mutableStateOf(false) }
    var segmentCount by remember { mutableStateOf(3f) }
    var segmentSpacing by remember { mutableStateOf(10.dp) }
    var segmentAngle by remember { mutableStateOf(0f) }
    val segmentColor by remember { mutableStateOf(Color.LightGray) }
    val segmentAlpha by remember { mutableStateOf(1f) }
    val progressAlpha by remember { mutableStateOf(1f) }
    val progressColor by remember { mutableStateOf(Green200) }
    var progress by remember { mutableStateOf(0f) }
    var progressState by remember { mutableStateOf(ProgressState.Idle) }
    val drawBehindProgress by remember {
        derivedStateOf { !enableBreathEffectAnimation || progressState == ProgressState.Progressing }
    }
    val animatedProgressAlpha = if (enableBreathEffectAnimation && progressState == ProgressState.Idle && progress.compareTo(segmentCount - 1) == 0) {
        val infiniteTransition = rememberInfiniteTransition()
        infiniteTransition.animateFloat(
            initialValue = progressAlpha,
            targetValue = progressAlpha,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = breathEffectAnimationDuration
                    progressAlpha.at(breathEffectAnimationDuration / 2)
                    (progressAlpha * 0.3f).at(3 * breathEffectAnimationDuration / 4)
                },
                repeatMode = RepeatMode.Restart
            )
        ).value
    } else {
        progressAlpha
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SegmentProgressBar(
            segmentCount = segmentCount.toInt(),
            modifier = Modifier.height(16.dp),
            spacing = segmentSpacing,
            angle = segmentAngle,
            progress = progress,
            segmentColor = SegmentColors(
                color = segmentColor,
                alpha = segmentAlpha
            ),
            progressColor = SegmentColors(
                color = progressColor,
                alpha = animatedProgressAlpha
            ),
            drawSegmentBehindProgress = drawBehindProgress,
            progressAnimationSpec = tween(
                durationMillis = progressAnimatedDuration,
                easing = LinearEasing
            ),
            onProgressChanged = { _: Float, _: SegmentCoordinates ->
                progressState = ProgressState.Progressing
            },
            onProgressFinished = {
                progressState = ProgressState.Idle
            }
        )

        Stepper(
            label = "Number of Segments: ${segmentCount.toInt()}",
            onMinus = {
                if (segmentCount > 1 && segmentCount > progress) segmentCount--
            },
            onPlus = {
                segmentCount++
            },
            modifier = Modifier.padding(top = 20.dp)
        )

        Stepper(
            label = "Progress: ${progress.toInt()}",
            onMinus = {
                if (progress > 0) progress--
            },
            onPlus = {
                if (progress < segmentCount) progress++
            },
            modifier = Modifier.padding(top = 20.dp)
        )

        RangePicker(
            title = "Spacing: ${segmentSpacing.value}",
            value = segmentSpacing.value,
            range = 0f..100f,
            onValueChanged = { segmentSpacing = Dp(it) },
            modifier = Modifier.padding(top = 20.dp)
        )

        RangePicker(
            title = "Angle: ${segmentAngle.toInt()}",
            value = segmentAngle,
            range = -60f..60f,
            onValueChanged = { segmentAngle = it },
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@Composable
fun Stepper(
    label: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(
            onClick = { onMinus() },
            modifier = Modifier.padding(start = 5.dp)
        ) {
            Text(text = "-")
        }
        OutlinedButton(
            onClick = { onPlus() },
            modifier = Modifier.padding(start = 5.dp)
        ) {
            Text(text = "+")
        }
    }
}

@Composable
fun RangePicker(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f)
        )
        Slider(
            value = value,
            onValueChange = onValueChanged,
            valueRange = range,
            modifier = Modifier.weight(1f),
            enabled = enabled
        )
    }
}