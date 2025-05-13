// File: screens/ReceiveScreen.kt
package com.example.warehouse.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse.data.AppDatabase
import com.example.warehouse.data.ReceiveTask
import com.example.warehouse.label.LabelData
import com.example.warehouse.label.LabelEditor
import com.example.warehouse.label.LabelLayout
import com.example.warehouse.util.ScanContract
import com.example.warehouse.viewmodel.ReceiveUiEvent
import com.example.warehouse.viewmodel.ReceiveUiEvent.*
import com.example.warehouse.viewmodel.ReceiveViewModel
import com.example.warehouse.viewmodel.ReceiveViewModelFactory
import java.util.Date
import kotlin.sequences.ifEmpty
import kotlin.toString
import com.example.warehouse.viewmodel.ReceiveUiEvent.ScanSuccess
import com.example.warehouse.viewmodel.ReceiveUiEvent.NotFound
import androidx.compose.runtime.LaunchedEffect
import android.widget.Toast
import com.example.warehouse.util.PrinterManager
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview


@Preview(showBackground = true)
@Composable
fun ReceiveScreen(
    mac: String = "",
    printEnabled: Boolean = true
) {
    val context = LocalContext.current
    val dao = AppDatabase.getInstance(context).taskDao()
    val scope = rememberCoroutineScope()
    // Передаём во ViewModel и текущий флаг печати
    val vm: ReceiveViewModel = viewModel(
        factory = ReceiveViewModelFactory(dao, mac)
    )
    val state by vm.uiState.collectAsState()
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let { vm.onScan(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ручной ввод + кнопка камеры
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.qrInput,
                onValueChange = { vm.onScan(it) },
                label = { Text("QR вручную") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    imeAction    = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = { vm.onScan(state.qrInput) }
                )
            )
            IconButton(onClick = {
                scanLauncher.launch(
                    ScanContract.Options().apply {
                        prompt      = "Наведите камеру на QR"
                        beepEnabled = true
                    }
                )
            }) {
                Icon(
                    imageVector     = Icons.Filled.CameraAlt,
                    contentDescription = "Сканировать"
                )
            }
        }

        // Обработка состояний
        when (val ev = state.event) {
            is ScanSuccess -> {
                // Две локальные переменные для qty и cell
                var qty  by remember { mutableStateOf(ev.qrData.initialQty) }
                var cell by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { vm.clearEvent() },
                    title   = { Text("Приёмка: ${ev.qrData.drawing}") },
                    text    = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Предварительный просмотр этикетки
                            LabelEditor(
                                layout = LabelLayout.Landscape57x40,
                                data   = LabelData(
                                    routeCard   = ev.qrData.routeCard,
                                    orderNumber = ev.qrData.orderNumber,
                                    drawing     = ev.qrData.drawing,
                                    name        = ev.qrData.name,
                                    quantity    = qty.toString(),
                                    date        = Date().toString(),
                                    qr          = ev.qrData.qrString,
                                    cellNumber  = cell
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .border(1.dp, Color.Gray)
                            )

                            // Поле для количества
                            OutlinedTextField(
                                value = qty.toString(),
                                onValueChange = { new ->
                                    qty = new.toIntOrNull() ?: ev.qrData.initialQty
                                },
                                label = { Text("Количество") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction    = ImeAction.Next,
                                    keyboardType = KeyboardType.Number
                                )
                            )

                            // Поле для ячейки
                            OutlinedTextField(
                                value = cell,
                                onValueChange = { cell = it },
                                label = { Text("Номер ячейки") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction    = ImeAction.Done,
                                    keyboardType = KeyboardType.Text
                                )
                            )

                            // Переключатель печати
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Печать этикетки")
                                Spacer(Modifier.weight(1f))
                                Switch(
                                    checked         = state.printEnabled,
                                    onCheckedChange = { vm.togglePrintEnabled(it) }
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val task = ReceiveTask(
                                    routeCard = ev.qrData.routeCard,
                                    orderNumber = ev.qrData.orderNumber,
                                    drawing = ev.qrData.drawing,
                                    name = ev.qrData.name,
                                    expectedQty = ev.qrData.initialQty,
                                    qty = qty,
                                    qrString = ev.qrData.qrString,
                                    cellNumber = cell,
                                    date = Date().toString(),
                                    order = ev.qrData.orderNumber,
                                    actualQty = qty
                                )

                                vm.confirmReceive(task, qty, cell)

                                if (state.printEnabled) {
                                    scope.launch {
                                        try {
                                            val session = PrinterManager.connect(context, mac)
                                            if (session != null) {
                                                PrinterManager.printReceiveLabel(session, task, ev.qrData) // добавляем qrData
                                                PrinterManager.close(session)
                                            } else {
                                                Toast.makeText(context, "Не удалось подключиться к принтеру", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Ошибка печати: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                vm.clearEvent()
                            }
                        ) {
                            Text("Сохранить")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { vm.clearEvent() }) {
                            Text("Отмена")
                        }
                    }
                )
            }

            is ScanSuccess -> {
                // Можно показать что-то вроде «Успешно сохранено»
                Text(
                    text  = "Приёмка сохранена",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            is ReceiveUiEvent.NotFound -> {
                Text(
                    text  = "Позиция не найдена",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                // Ничего не показываем в начальном состоянии
            }
        }
    }
}
