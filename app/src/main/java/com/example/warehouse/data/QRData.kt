package com.example.warehouse.data

/**
 * Результат распознавания QR:
 * маршрутная карта = номер заказа = чертёж = наименование
 * initialQty — запрошенное количество
 */
data class QRData(
    val routeCard: String,
    val orderNumber: String,  // убедитесь что это поле существует
    val drawing: String,
    val name: String,
    val initialQty: Int,
    val qrString: String
)
