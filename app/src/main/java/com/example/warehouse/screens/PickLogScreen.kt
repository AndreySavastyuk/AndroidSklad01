package com.example.warehouse.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse.data.PickLog
import com.example.warehouse.ui.theme.Dimens
import com.example.warehouse.viewmodel.PickLogViewModel
import com.example.warehouse.viewmodel.PickLogViewModelFactory
import com.example.warehouse.data.AppDatabase
import com.example.warehouse.label.LabelEditor
import com.example.warehouse.label.LabelLayout
import com.example.warehouse.label.LabelData

/**
 * Экран истории выдач деталей.
 */
import androidx.compose.ui.tooling.preview.Preview


@Preview(showBackground = true)
@Composable
fun PickLogScreen() {
    val context = LocalContext.current
    val dao = AppDatabase.getInstance(context).pickDao()
    val vm: PickLogViewModel = viewModel(
        factory = PickLogViewModelFactory(dao)
    )

    val logs by vm.logs.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                vm.onQueryChange(it)
            },
            label = { Text("Поиск заказа/чертежа") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.inputHeight),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { vm.onQueryChange(query) })
        )

        Spacer(modifier = Modifier.height(Dimens.elementSpacing))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
        ) {
            items(logs) { log ->
                PickLogItem(log)
                Divider()
            }
        }
    }
}

@Composable
private fun PickLogItem(log: PickLog) {
    Text(
        text = "${log.timestamp} | ${log.order} | ${log.drawing} | ${log.name} | ${log.pickedQty} шт. | печать: ${if (log.printed) "да" else "нет"}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(Dimens.elementSpacing)
    )
}