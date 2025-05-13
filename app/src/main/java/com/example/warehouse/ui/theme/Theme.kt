package com.example.warehouse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SeedColor = Color(0xFF64B5F6)

@Composable
fun WarehouseTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            dynamicLightColorScheme(LocalContext.current)
        else -> lightColorScheme(
            primary       = SeedColor,
            onPrimary     = Color.White,
            primaryContainer = SeedColor.copy(alpha = .2f),

            secondary     = Color(0xFF81C784),   // мягкий зелёный
            onSecondary   = Color.White,

            tertiary      = Color(0xFFFFB74D),   // мягкий оранжевый
            onTertiary    = Color.Black,

            error         = Color(0xFFE57373),
            onError       = Color.White,

            background    = Color(0xFFCBCBCB),
            surface       = Color(0xFFFFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { content() }
    }
}
