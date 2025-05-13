package com.example.warehouse.viewmodel

import com.example.warehouse.data.ReceiveTask
import com.example.warehouse.data.QRData

/**
 * События экрана приёма: либо успешно распознан QR, либо не найден
 */
sealed class ReceiveUiEvent {
    object None      : ReceiveUiEvent()
    object NotFound  : ReceiveUiEvent()
    data class Success(val task: ReceiveTask) : ReceiveUiEvent()
    data class ScanSuccess(val qrData: QRData) : ReceiveUiEvent()
}