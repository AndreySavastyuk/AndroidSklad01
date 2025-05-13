package com.example.warehouse.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.PickDao
import com.example.warehouse.data.PickLog
import com.example.warehouse.data.PickTask
import com.example.warehouse.util.QRParser
import com.example.warehouse.data.QRData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlinx.coroutines.flow.update


sealed class PickUiEvent {
    object Idle : PickUiEvent()
    data class Success(val task: PickTask) : PickUiEvent()
    object NotFound : PickUiEvent()
}

data class PickUiState(
    val qrInput: String = "",
    val event: PickUiEvent = PickUiEvent.Idle,
    val printEnabled: Boolean = true,
    val tempQty: Int = 0
)

class PickViewModel(
    private val dao: PickDao,
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(PickUiState())
    val uiState: StateFlow<PickUiState> = _uiState

    /** Вызывается, когда пользователь меняет количество в диалоге. */
    fun onQtyChange(qty: Int) {
        _uiState.update { it.copy(tempQty = qty) }
    }

    /**
     * Обработка сканирования QR: проверяем drawing в списке задач.
     */
    fun onScan(qr: String) {
        val data: QRData? = QRParser.parse(qr.trim())
        viewModelScope.launch {
            if (data == null) {
                playErrorSound()
                _uiState.value = _uiState.value.copy(event = PickUiEvent.NotFound)
            } else {
                val tasks = dao.getTasks().first()
                val task = tasks.find { it.drawing == data.drawing }
                if (task == null) {
                    playErrorSound()
                    _uiState.value = _uiState.value.copy(event = PickUiEvent.NotFound)
                } else {
                    playOkSound()
                    _uiState.value = _uiState.value.copy(
                        event = PickUiEvent.Success(task)
                    )
                }
            }
        }
    }

    /**
     * Подтверждение выдачи: сохраняем лог, отмечаем задачу, печатаем при включенном флаге.
     */
    fun confirmPick(task: PickTask, qty: Int, print: Boolean, mac: String) {
        viewModelScope.launch {
            dao.insertLog(
                PickLog(
                    drawing = task.drawing,
                    order = task.order,
                    name = task.name,
                    pickedQty = qty,
                    printed = print,
                    timestamp = LocalDateTime.now().toString()
                )
            )
            dao.markCompleted(task.drawing)
            if (print) {
                // Здесь вызываем PrintHelper из util (нужен context и mac)
                com.example.warehouse.util.PrintHelper.printLabel(
                    context = context,
                    activity = context as android.app.Activity,
                    mac = mac,
                    qrText = "${task.drawing}",
                    order = task.order,
                    drawing = task.drawing,
                    name = task.name,
                    qty = qty,
                    date = LocalDateTime.now().toLocalDate().toString()
                )
            }
            // Сбросим событие в Idle
            _uiState.value = _uiState.value.copy(event = PickUiEvent.Idle)
        }
    }

    fun togglePrintEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(printEnabled = enabled)
    }

    private fun playOkSound() { /* ... */ }
    private fun playErrorSound() { /* ... */ }
}
