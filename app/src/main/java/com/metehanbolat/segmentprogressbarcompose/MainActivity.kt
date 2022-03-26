package com.metehanbolat.segmentprogressbarcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.metehanbolat.segmentprogressbarcompose.ui.theme.SegmentProgressBarComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SegmentProgressBarComposeTheme {

            }
        }
    }
}