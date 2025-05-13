package com.example.warehouse.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.warehouse.data.TemplateType


/**
 * Конфигурация принтера
 */
data class PrinterConfig(
    var name: String,
    var mac: String,
    var templateType: TemplateType = TemplateType.TSPL,
    var templateFile: String = "tspl_label.prn"
)

/**
 * Экран настроек подключения принтеров
 *
 * @param initialPrinters   – начальный список принтеров
 * @param initialSelected   – индекс выбранного принтера по умолчанию
 * @param initialEnabled    – первоначальное состояние флага печати
 * @param onSave            – вызывается при сохранении: возвращает список принтеров,
 *                            индекс выбранного принтера и флаг печати
 * @param onCancel          – вызывается при отмене
 */
@Composable
fun SettingsScreen(
    initialPrinters: List<PrinterConfig> = listOf(
        PrinterConfig("Xprinter V3 BT", "10:23:81:5B:DA:29")
    ),
    initialSelected: Int = 0,
    initialEnabled: Boolean = true,
    onSave: (printers: List<PrinterConfig>, selectedIndex: Int, printEnabled: Boolean) -> Unit,
    onCancel: () -> Unit
) {
    // Состояние списка принтеров
    val printers = remember { initialPrinters.toMutableStateList() }
    var selectedIndex by remember { mutableStateOf(initialSelected.coerceIn(printers.indices)) }
    var printEnabled by remember { mutableStateOf(initialEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Настройки принтеров", fontSize = 20.sp)

        Text(text = "Список принтеров:", fontSize = 16.sp)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(printers) { index, printer ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index }
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = printer.name,
                            onValueChange = { printer.name = it },
                            label = { Text("Имя принтера") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = printer.mac,
                            onValueChange = { printer.mac = it },
                            label = { Text("MAC-адрес") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    IconButton(onClick = {
                        printers.removeAt(index)
                        // скорректировать выбор при удалении
                        if (selectedIndex >= printers.size) {
                            selectedIndex = printers.size - 1
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить принтер")
                    }
                }
            }
        }

        Button(
            onClick = {
                // добавить новый пустой принтер
                printers.add(PrinterConfig("", ""))
                selectedIndex = printers.lastIndex
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить принтер")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Печать включена")
            Spacer(Modifier.weight(1f))
            Switch(
                checked = printEnabled,
                onCheckedChange = { printEnabled = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onSave(printers.toList(), selectedIndex, printEnabled) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Сохранить")
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        initialPrinters = listOf(PrinterConfig("Xprinter V3 BT", "10:23:81:5B:DA:29")),
        initialSelected = 0,
        initialEnabled = true,
        onSave = { _, _, _ -> },
        onCancel = {}
    )
}
