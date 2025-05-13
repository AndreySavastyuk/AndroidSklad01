package com.example.warehouse.label

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.*

/**
 * Preview для визуализации макета этикетки в Android Studio Layout Editor
 */
@Preview(showBackground = true)
@Composable
fun LabelPreviewScreen() {
    val sampleData = LabelData(
        routeCard   = "1672",
        orderNumber = "2023/016",
        drawing     = "НЗ.КШ.040.25.002-01",
        name        = "Втулка",
        quantity    = "5",
        date        = "2024-06-07",
        qr          = "1672=2023/016=НЗ.01.02.54=Втулка",
        cellNumber  = "A67"
    )
    LabelEditor(
        layout = LabelLayout.Landscape57x40,
        data   = sampleData,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    )
}
