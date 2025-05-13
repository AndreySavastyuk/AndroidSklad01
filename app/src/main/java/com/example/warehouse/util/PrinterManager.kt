// File: PrinterManager.kt
package com.example.warehouse.print

import android.content.Context
import com.pos.sdk.port.POSConnect
import com.pos.sdk.port.IDeviceConnection
import com.pos.sdk.port.IConnectListener
import com.pos.sdk.tspl.TSPLPrinter
import com.pos.sdk.tspl.TSPLConst
import com.pos.sdk.port.POSConst
import com.example.warehouse.label.LabelData

object PrinterManager {
    private var connection: IDeviceConnection? = null
    private var printer: TSPLPrinter? = null

    /**
     * Инициализация SDK (вызывать в Application.onCreate)
     */
    fun init(context: Context) {
        POSConnect.init(context.applicationContext)  // :contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
    }

    /**
     * Подключаемся по MAC к Bluetooth‑принтеру
     */
    fun connect(mac: String, onResult: (success: Boolean, msg: String) -> Unit) {
        connection = POSConnect.createDevice(POSConst.DEVICE_TYPE_BLUETOOTH)  // :contentReference[oaicite:2]{index=2}:contentReference[oaicite:3]{index=3}
        connection!!.connect(mac, object : IConnectListener {
            override fun onStatus(code: Int, connectInfo: String, msg: String) {
                if (code == POSConst.CONNECT_SUCCESS) {
                    printer = TSPLPrinter(connection!!)  // :contentReference[oaicite:4]{index=4}:contentReference[oaicite:5]{index=5}
                    onResult(true, "Подключено")
                } else {
                    onResult(false, "Ошибка подключения: $msg")
                }
            }
        })
    }

    /**
     * Печать одной этикетки из данных LabelData
     */
    fun printLabel(data: LabelData, copies: Int = 1) {
        printer?.apply {
            // Настраиваем метку 57×40 мм
            sizeMm(57, 40)                   // :contentReference[oaicite:6]{index=6}:contentReference[oaicite:7]{index=7}
            gapMm(2.0, 0.0)                  // :contentReference[oaicite:8]{index=8}:contentReference[oaicite:9]{index=9}
            density(8)                       // :contentReference[oaicite:10]{index=10}:contentReference[oaicite:11]{index=11}
            cls()                            // очистить буфер :contentReference[oaicite:12]{index=12}:contentReference[oaicite:13]{index=13}
            reference(0, 0)                  // точка отсчёта :contentReference[oaicite:14]{index=14}:contentReference[oaicite:15]{index=15}

            // Нарисуем рамку ячейки
            box(10, 10, 200, 120, 2)         // :contentReference[oaicite:16]{index=16}:contentReference[oaicite:17]{index=17}

            // Чертёж (номер детали)
            text(20, 20, TSPLConst.FNT_24_32, data.drawing)  // :contentReference[oaicite:18]{index=18}:contentReference[oaicite:19]{index=19}

            // Наименование
            text(20, 50, TSPLConst.FNT_16_24, data.name)     // :contentReference[oaicite:20]{index=20}:contentReference[oaicite:21]{index=21}

            // Номер заказа
            text(20, 80, TSPLConst.FNT_16_24, data.orderNumber) // :contentReference[oaicite:22]{index=22}:contentReference[oaicite:23]{index=23}

            // Ячейка
            text(20, 110, TSPLConst.FNT_16_24, data.cellNumber) // :contentReference[oaicite:24]{index=24}:contentReference[oaicite:25]{index=25}

            // QR‑код
            qrcode(250, 20, /*ecLevel*/ TSPLConst.EC_LEVEL_L, /*cellWidth*/ 4, /*rotation*/ 0, data.qr) // :contentReference[oaicite:26]{index=26}:contentReference[oaicite:27]{index=27}

            // Отправляем на печать
            print(copies)                    // :contentReference[oaicite:28]{index=28}:contentReference[oaicite:29]{index=29}
        }
    }

    /**
     * Отключение от принтера
     */
    fun disconnect() {
        connection?.close()  // :contentReference[oaicite:30]{index=30}:contentReference[oaicite:31]{index=31}
        connection = null
        printer = null
    }
}
