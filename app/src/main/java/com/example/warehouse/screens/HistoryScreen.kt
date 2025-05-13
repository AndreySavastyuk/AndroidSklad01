package com.example.warehouse.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse.data.Task
import com.example.warehouse.ui.theme.Dimens
import com.example.warehouse.viewmodel.HistoryViewModel
import com.example.warehouse.viewmodel.HistoryViewModelFactory
import com.example.warehouse.data.AppDatabase
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun HistoryScreen() {
    // Получаем контекст и DAO через AppDatabase
    val context = LocalContext.current
    val dao = AppDatabase.getInstance(context).taskDao()
    val vm: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(dao)
    )

    // Состояние списка задач и строка поиска
    val tasks by vm.tasks.collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { new ->
                query = new
                vm.search(new)
            },
            label = { Text("Поиск заказа или чертежа") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.inputHeight),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { vm.search(query) }),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(Dimens.elementSpacing))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
        ) {
            items(tasks) { task ->
                HistoryItem(task)
                Divider()
            }
        }
    }
}

@Composable
private fun HistoryItem(task: Task) {
    Text(
        text = "${task.order} | ${task.drawing} | ${task.name} | ${task.quantity} шт. | ${task.date}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(Dimens.elementSpacing)
    )
}
