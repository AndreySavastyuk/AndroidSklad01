package com.example.warehouse

import android.app.Application
import com.example.warehouse.data.AppDatabase
import net.posprinter.POSConnect
import com.example.warehouse.util.PrinterManager

/**
 * Application‑класс, в котором:
 * 1) инициализируется база данных
 * 2) инициализируется POS‑SDK принтера
 *
 * Не забудьте прописать его в AndroidManifest.xml:
 * <application android:name=".WarehouseApp" …>
 */
class WarehouseApp : Application() {

    companion object {
        /** Синглтон базы данных, если вам удобен статический доступ */
        lateinit var db: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        PrinterManager.init(this)
        // 1) Инициализируем базу данных
        db = AppDatabase.getInstance(this)

        // 2) Инициализируем Xprinter SDK
        //    Это обязательно перед любым вызовом POSConnect.createDevice(...)
        POSConnect.init(this)
    }
}
