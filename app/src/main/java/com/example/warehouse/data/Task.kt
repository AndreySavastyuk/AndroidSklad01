package com.example.warehouse.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val order: String,
    val drawing: String,
    val name: String,
    val quantity: Int,
    val date: String
)