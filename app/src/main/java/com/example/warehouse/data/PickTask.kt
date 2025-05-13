package com.example.warehouse.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pick_tasks")
data class PickTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routeCard: String,
    val orderNumber: String,
    val order: String,
    val drawing: String,
    val name: String,
    val qty: Int,
    val requiredQty: Int,
    val qrData: QRData?,
    val cellNumber: String,
    val completed: Boolean,
    val date: String
)