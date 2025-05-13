package com.example.warehouse

import android.app.Application
import net.posprinter.POSConnect

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // инициализируем низкоуровневое соединение (Bluetooth/USB/Wi‑Fi)
        POSConnect.init(this)
    }
}