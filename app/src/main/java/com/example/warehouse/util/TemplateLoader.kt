package com.example.warehouse.util

import android.content.Context

object TemplateLoader {
    private val cache = mutableMapOf<String,String>()

    /** Читает текст шаблона из assets */
    fun load(context: Context, filename: String): String =
        cache.getOrPut(filename) {
            context.assets.open(filename)
                .bufferedReader()
                .use { it.readText() }
        }

    /** Подставляет в rawTpl все маркеры ${…} из data */
    fun fill(rawTpl: String, data: LabelData): String {
        return rawTpl
            .replace("\${routeCard}",   data.routeCard)
            .replace("\${drawing}",     data.drawing)
            .replace("\${name}",        data.name)
            .replace("\${orderNumber}", data.orderNumber)
            .replace("\${cellNumber}",  data.cellNumber)
            .replace("\${date}",        data.date)
            .replace("\${qrData}",      data.qrPayload)
    }
}