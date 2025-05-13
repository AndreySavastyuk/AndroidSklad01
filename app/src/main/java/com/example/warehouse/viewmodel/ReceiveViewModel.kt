package com.example.warehouse.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.QRData
import com.example.warehouse.data.ReceiveTask
import com.example.warehouse.data.TaskDao
import com.example.warehouse.label.LabelData
import com.example.warehouse.util.PrinterManager
import com.example.warehouse.viewmodel.ReceiveUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.warehouse.util.QRParser
import kotlinx.coroutines.flow.asStateFlow
import kotlin.toString


/**
 * Состояние экрана приёма:
 * qrInput — введённый QR (если вводили вручную)
 * event — результат сканирования (успех или не найден)
 * qty — текущее введённое количество
 * cell — введённый номер ячейки
 */
data class ReceiveUiState(
    val qrInput: String = "",
    val event: ReceiveUiEvent? = null,
    val printEnabled: Boolean   = true,
    val qty: Int = 0,
    val cell: String = ""
)

class ReceiveViewModel(
    private val dao: TaskDao,
    private val mac: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiveUiState())
    val uiState: StateFlow<ReceiveUiState> = _uiState.asStateFlow()

    fun clearEvent() {
        _uiState.update { it.copy(event = null) }
    }
    fun togglePrintEnabled(enabled: Boolean) {
        _uiState.update { it.copy(printEnabled = enabled) }
    }
    /**
     * Обработка сканирования или ручного ввода QR.
     */
    fun onScan(qr: String) {
        // парсим QR, функция QRParser должна вернуть QRData?
        val data: QRData? = QRParser.parse(qr)
        if (data != null) {
            _uiState.update { it.copy(
                qrInput = qr,
                event = ReceiveUiEvent.ScanSuccess(data),
                qty = 0,
                cell = ""
            ) }
        } else {
            _uiState.update { it.copy(
                event = ReceiveUiEvent.NotFound
            ) }
        }
    }

    /** Пользователь изменил количество */
    fun onQtyChange(newQty: Int) {
        _uiState.update { it.copy(qty = newQty) }
    }

    /** Пользователь изменил номер ячейки */
    fun onCellChange(newCell: String) {
        _uiState.update { it.copy(cell = newCell) }
    }

    /** Подтверждение приёма: сохраняем в БД */
    fun confirmReceive(task: ReceiveTask, qty: Int, cell: String) {
        val print = _uiState.value.printEnabled
        viewModelScope.launch {
            val ev = _uiState.value.event
            if (ev is ReceiveUiEvent.Success) {
                val d = ev.task
                // создаём задачу приёма
                val task = ReceiveTask(
                    routeCard = d.routeCard,
                    order = d.order,
                    drawing = d.drawing,
                    name = d.name,
                    qty = qty,
                    date = LocalDate.now().toString(),
                    cellNumber = cell,
                    qrString = d.qrString,
                    orderNumber = d.orderNumber,
                    expectedQty = d.expectedQty,
                    actualQty = qty,
                )
                dao.insertReceive(task)

                // Печать этикетки
                if (print) {
                    val labelData = LabelData(
                        routeCard = d.routeCard,
                        drawing = d.drawing,
                        name = d.name,
                        orderNumber = d.order,
                        cellNumber = cell,
                        date = LocalDate.now().toString(),
                        qrPayload = listOf(
                            d.routeCard,
                            d.drawing,
                            d.name,
                            d.order,
                            cell,
                            LocalDate.now().toString()
                        ).joinToString(";")
                    )

                    PrinterManager.init(context, printerConfig)
                    PrinterManager.printLabel(context, printerConfig, labelData)
                }
            }
        }
    }