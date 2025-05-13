package com.example.warehouse.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReceiveTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // из QRData
    val routeCard:   String,
    val orderNumber: String, // единое имя, без дублирования
    val order: String,
    val drawing:     String,
    val name:        String,

    // сколько ожидалось и сколько реально приняли
    val expectedQty: Int,
    val actualQty:   Int,

    // сам QR для возможного повторного парсинга или журнала
    val qrString:    String,

    // куда положили
    val cellNumber:  String,

    // когда
    val date:        String,

    val qty: Int
)
