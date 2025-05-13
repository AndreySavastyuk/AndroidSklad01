package com.example.warehouse.label

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit


/**
 * Основной класс для редактирования бирки.
 * Вызывает нужный макет по переданному layout.
 */
@Composable
fun LabelEditor(
    layout: LabelLayout,
    data: LabelData,
    modifier: Modifier = Modifier
) {
    when (layout) {
        LabelLayout.Landscape57x40 -> LabelVariant1(data = data, modifier = modifier)
    }
}

/**
 * Вариант оформления макета бирки 57×40 мм.
 * Можно добавить LabelVariant2..5 аналогично.
 */
@Preview(showBackground = true)
@Composable
fun LabelVariant1Preview() {
    LabelVariant1(
        data = LabelData(
            drawing = "123-456",
            qr = "QR123456",
            name = "Деталь",
            orderNumber = "Заказ 789",
            cellNumber = "Ячейка 42",
            routeCard = "Маршрут 1",
            quantity = "10",
            date = "01.01.2024"
        )
    )
}
@Composable
fun LabelVariant1(
    data: LabelData,
    modifier: Modifier = Modifier
) {
    LabelBase(
        data = data,
        modifier = modifier,
        params = LabelParams(
            drawingOffset = 17f to 11f,
            drawingSize = 20.sp,
            qrOffset = 12f to 73f,
            qrSizeMm = 96f / LocalDensity.current.density, // 96dp
            nameOffset = 304f to 71f,
            nameSize = 16.sp,
            orderOffset = 261f to 113f,
            orderSize = 16.sp,
            cellBoxOffset = 257f to 182f,
            cellBoxSize = 186f to 122f,
            cellTextOffset = 266f to 188f,
            cellTextSize = 18.sp,
            borderShape = RoundedCornerShape(4.dp),
            borderWidth = 1.dp
        )
    )
}

/**
 * Размеры и позиции элементов в мм.
 */
sealed class LabelLayout(val widthMm: Float, val heightMm: Float) {
    object Landscape57x40 : LabelLayout(57f, 40f)
}

data class LabelData(
    val drawing: String,
    val qr: String,
    val name: String,
    val orderNumber: String,
    val cellNumber: String,
    val routeCard : String,
    val quantity: String,
    val date: String
)

/**
 * Параметры базового макета.
 */
data class LabelParams(
    val drawingOffset: Pair<Float, Float>,
    val drawingSize: TextUnit,
    val qrOffset: Pair<Float, Float>,
    val qrSizeMm: Float,
    val nameOffset: Pair<Float, Float>,
    val nameSize: TextUnit,
    val orderOffset: Pair<Float, Float>,
    val orderSize: TextUnit,
    val cellBoxOffset: Pair<Float, Float>,
    val cellBoxSize: Pair<Float, Float>,
    val cellTextOffset: Pair<Float, Float>,
    val cellTextSize: TextUnit,
    val borderShape: RoundedCornerShape? = null,
    val borderWidth: Dp = 0.dp,
    val accentColor: Color = Color.Black
)

/**
 * Общая функция-основа, рисует прямоугольник и элементы по координатам.
 */
@Composable
fun LabelBase(
    data: LabelData,
    modifier: Modifier = Modifier,
    params: LabelParams
) {
    val density = LocalDensity.current
    // конвертация мм в dp: (mm/25.4)*dpi -> px, затем .dp
    fun mmToDp(mm: Float): Dp =
        ((mm / 25.4f) * density.density * 160f).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(params.widthRatio() / params.heightRatio())
            .then(
                params.borderShape?.let { shape ->
                    Modifier.border(params.borderWidth, params.accentColor, shape)
                } ?: Modifier
            )
            .background(Color.White)
    ) {
        // номер детали
        Text(
            text = data.drawing,
            fontSize = params.drawingSize,
            fontWeight = FontWeight.Bold,
            color = params.accentColor,
            modifier = Modifier
                .offset(
                    x = mmToDp(params.drawingOffset.first),
                    y = mmToDp(params.drawingOffset.second)
                )
        )

        // QR
        val qrDp = mmToDp(params.qrSizeMm)
        val qrBitmap = remember(data.qr) {
            val px = with(density) { qrDp.toPx().toInt() }
            BarcodeEncoder().createBitmap(
                QRCodeWriter().encode(data.qr, BarcodeFormat.QR_CODE, px, px)
            )
        }
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(
                    x = mmToDp(params.qrOffset.first),
                    y = mmToDp(params.qrOffset.second)
                )
                .size(qrDp)
        )

        // наименование
        Text(
            text = data.name,
            fontSize = params.nameSize,
            modifier = Modifier.offset(
                x = mmToDp(params.nameOffset.first),
                y = mmToDp(params.nameOffset.second)
            )
        )

        // номер заказа
        Text(
            text = data.orderNumber,
            fontSize = params.orderSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(
                x = mmToDp(params.orderOffset.first),
                y = mmToDp(params.orderOffset.second)
            )
        )

        // рамка ячейки
        Box(
            Modifier
                .offset(
                    x = mmToDp(params.cellBoxOffset.first),
                    y = mmToDp(params.cellBoxOffset.second)
                )
                .size(
                    width = mmToDp(params.cellBoxSize.first),
                    height = mmToDp(params.cellBoxSize.second)
                )
                .border(1.dp, Color.Black)
        )

        // текст ячейки
        Text(
            text = data.cellNumber,
            fontSize = params.cellTextSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(
                x = mmToDp(params.cellTextOffset.first),
                y = mmToDp(params.cellTextOffset.second)
            )
        )
    }
}

// утилита для соотношения сторон при aspectRatio
private fun LabelParams.widthRatio() = this.cellBoxSize.first / this.cellBoxSize.second
private fun LabelParams.heightRatio() = this.cellBoxSize.first / this.cellBoxSize.second
