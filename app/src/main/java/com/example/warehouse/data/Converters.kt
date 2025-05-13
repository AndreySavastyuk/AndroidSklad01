// File: data/Converters.kt
package com.example.warehouse.data

import androidx.room.TypeConverter
import com.example.warehouse.util.QRParser
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    /** Сериализуем QRData в JSON-строку */
    @TypeConverter
    fun fromQRData(qrData: QRData?): String? =
        qrData?.let { gson.toJson(it) }

    /** Десериализуем JSON-строку обратно в QRData */
    @TypeConverter
    fun toQRData(value: String?): QRData? =
        value?.let { gson.fromJson(it, QRData::class.java) }
}
