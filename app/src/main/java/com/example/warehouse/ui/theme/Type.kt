package com.example.warehouse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

/**
 * Типографика приложения.
 */
val Typography = Typography(
    titleLarge  = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
    bodyLarge   = TextStyle(fontSize = 18.sp),
    bodyMedium  = TextStyle(fontSize = 16.sp)
)
