// ========================================
// File: screens/PickScreen.kt
// ========================================
package com.example.warehouse.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse.data.AppDatabase
import com.example.warehouse.data.PickTask
import com.example.warehouse.label.LabelData
import com.example.warehouse.label.LabelEditor
import com.example.warehouse.label.LabelLayout
import com.example.warehouse.util.ScanContract
import com.example.warehouse.viewmodel.PickUiEvent
import com.example.warehouse.viewmodel.PickUiState
import com.example.warehouse.viewmodel.PickViewModel
import com.example.warehouse.viewmodel.PickViewModelFactory
import java.util.*

/**
 * Контейнер с UI-логикой, используемый для Preview и реального экрана.
 */
@Composable
fun PickScreenContent(
    state: PickUiState,
    scanLauncher: () -> Unit = {},
    onScan: (String) -> Unit = {},
    onQtyChange: (Int) -> Unit = {},
    onPrintToggle: (Boolean) -> Unit = {},
    onConfirm: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Поле ввода QR и иконка камеры
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.qrInput,
                onValueChange = onScan,
                label = { Text("QR вручную") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(onDone = { onScan(state.qrInput) })
            )
            IconButton(onClick = scanLauncher) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Сканировать")
            }
        }

        // Результаты сканирования
        when (val ev = state.event) {
            is PickUiEvent.Success -> {
                var qty by remember { mutableStateOf(ev.task.requiredQty) }
                AlertDialog(
                    onDismissRequest = { /* noop */ },
                    title = { Text("Выдача: ${ev.task.drawing}") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Поле количества
                            Text("Требуется: ${ev.task.requiredQty}")
                            OutlinedTextField(
                                value = qty.toString(),
                                onValueChange = { new ->
                                    val parsed = new.toIntOrNull() ?: ev.task.requiredQty
                                    qty = parsed
                                    onQtyChange(parsed)
                                },
                                label = { Text("Количество") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            // Переключатель печати
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Печать этикетки")
                                Spacer(Modifier.weight(1f))
                                Switch(
                                    checked = state.printEnabled,
                                    onCheckedChange = onPrintToggle
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { onConfirm(qty) }) {
                            Text("Печать")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { /* noop */ }) {
                            Text("Отмена")
                        }
                    }
                )
            }
            is PickUiEvent.NotFound -> {
                Text(
                    "Позиция не найдена",
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                // начальное состояние
            }
        }
    }
}

/**
 * Реальный экран, связывающий ViewModel и PickScreenContent.
 */
@Composable
fun PickScreen(mac: String) {
    val context = LocalContext.current
    val dao = AppDatabase.getInstance(context).pickDao()
    val vm: PickViewModel = viewModel(factory = PickViewModelFactory(dao, context))
    val state by vm.uiState.collectAsState()
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let { vm.onScan(it) }
    }

    PickScreenContent(
        state = state,
        scanLauncher = {
            scanLauncher.launch(
                ScanContract.Options().apply {
                    prompt = "Наведите камеру на QR"
                    beepEnabled = true
                }
            )
        },
        onScan = vm::onScan,
        onQtyChange = vm::onQtyChange,
        onPrintToggle = vm::togglePrintEnabled,
        onConfirm = { qty ->
            val task = (state.event as? PickUiEvent.Success)?.task
            if (task != null) vm.confirmPick(task, qty, state.printEnabled, mac)
        }
    )
}

/**
 * Preview для PickScreenContent без ошибок рендера.
 */
@Preview(showBackground = true)
@Composable
fun PickScreenPreview() {
    val sample = PickTask(
        id = 1,
        routeCard = "1672",
        orderNumber = "2023/016",
        order = "2023/016",
        drawing = "НЗ.КШ.040.25.002-01",
        name = "Втулка",
        qty = 0,
        requiredQty = 5,
        qrData = null,
        cellNumber = "A67",
        completed = false,
        date = "2025-05-11"
    )
    val state = PickUiState(
        qrInput = "",
        event = PickUiEvent.Success(sample),
        printEnabled = true
    )
    PickScreenContent(state = state)
}
