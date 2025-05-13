package com.example.warehouse.util

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

object PrintHelper {
    private const val REQUEST_BT_PERMS = 1001
    private val PRINT_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    /**
     * Печать этикетки по Bluetooth-мак-адресу.
     * Если нет разрешений BLUETOOTH_SCAN или BLUETOOTH_CONNECT, запросит их.
     */
    suspend fun printLabel(
        context: Context,
        activity: Activity,
        mac: String,
        qrText: String,
        order: String,
        drawing: String,
        name: String,
        qty: Int,
        date: String
    ) {
        // Проверяем разрешения
        val needed = listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        ).filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (needed.isNotEmpty()) {
            // Запрос разрешений у пользователя
            ActivityCompat.requestPermissions(activity, needed, REQUEST_BT_PERMS)
            Toast.makeText(context, "Нужны разрешения Bluetooth для печати", Toast.LENGTH_LONG).show()
            return
        }

        withContext(Dispatchers.IO) {
            val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            if (adapter == null) {
                showToast(context, "Bluetooth не поддерживается на устройстве")
                return@withContext
            }

            if (!adapter.isEnabled) {
                showToast(context, "Включите Bluetooth для печати")
                return@withContext
            }

            // Остановим сканирование, если оно идёт
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }

            try {
                val device: BluetoothDevice = adapter.getRemoteDevice(mac)
                val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(PRINT_UUID)
                socket.connect()

                val stream: OutputStream? = socket.outputStream
                // Подготовка текста для печати (в кодировке принтера)
                val data = buildString {
                    append(qrText).append("\n")
                    append(order).append(" \t")
                    append(drawing).append("\n")
                    append(name).append(" x$qty ").append(date).append("\n")
                }
                stream?.write(data.toByteArray())
                stream?.flush()
                socket.close()
                showToast(context, "Этикетка отправлена на принтер")
            } catch (e: IOException) {
                showToast(context, "Ошибка печати: ${e.localizedMessage}")
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        // Toast можно показывать из фонового потока через main
        android.os.Handler(context.mainLooper).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
