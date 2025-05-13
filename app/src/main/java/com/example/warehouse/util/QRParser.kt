package com.example.warehouse.util

import com.example.warehouse.data.QRData

/**
 * Разбирает строку вида "1672=№2023/016=НЗ.01.02.54=Втулка"
 * и возвращает QRData, либо null, если формат неверный.
 */
object QRParser {
    fun parse(raw: String): QRData? {
        val parts = raw.split('=').map { it.trim() }
        if (parts.size != 4) return null
        // убираем знак №, если есть
        val routeCard = parts[0]
        val order    = parts[1].removePrefix("№")
        val drawing  = parts[2]
        val name     = parts[3]
        // initialQty для ReceiveScreen мы возьмём в диалоге, здесь можно 0
        return QRData(
            routeCard = routeCard,
            orderNumber = order,
            drawing = drawing,
            name = name,
            initialQty = 0,
            qrString = raw  // добавляем исходную строку QR-кода
        )
    }
}