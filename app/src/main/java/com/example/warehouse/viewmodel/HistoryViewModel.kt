package com.example.warehouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.Task
import com.example.warehouse.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val dao: TaskDao
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        loadAll()
    }

    private fun loadAll() {
        dao.getAll().onEach { list ->
            _tasks.value = list
        }.launchIn(viewModelScope)
    }

    /** Фильтрует записи по запросу (часть заказа или чертежа) */
    fun search(query: String) {
        dao.search(query).onEach { list ->
            _tasks.value = list
        }.launchIn(viewModelScope)
    }
}
