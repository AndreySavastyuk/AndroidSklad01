package com.example.warehouse.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

/**
 * Утилита для определения класса ширины экрана (Compact, Medium, Expanded).
 */
@Composable
fun rememberWindowSizeClass(): WindowWidthSizeClass {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp.dp
    return when {
        widthDp < 600.dp -> WindowWidthSizeClass.Compact
        widthDp < 840.dp -> WindowWidthSizeClass.Medium
        else             -> WindowWidthSizeClass.Expanded
    }
}