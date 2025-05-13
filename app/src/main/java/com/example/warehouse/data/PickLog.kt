package com.example.warehouse.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Лог фактических выдач деталей.
 */
@Entity(tableName = "pick_log")
data class PickLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val drawing: String,
    val order: String,
    val name: String,
    val pickedQty: Int,
    val printed: Boolean,
    val timestamp: String // ISO или другой формат
)
