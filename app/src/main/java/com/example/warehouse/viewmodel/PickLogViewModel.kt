package com.example.warehouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.PickLog
import com.example.warehouse.data.PickDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана истории выдач.
 * Поддерживает поиск по заказу или чертежу.
 */
class PickLogViewModel(
    private val dao: PickDao
) : ViewModel() {

    // Поле поиска
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Поток записей с фильтрацией
    val logs: StateFlow<List<PickLog>> = _query
        .flatMapLatest { q ->
            dao.getLog().map { list ->
                if (q.isBlank()) list
                else list.filter { it.order.contains(q, true) || it.drawing.contains(q, true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    /**
     * Установить строку поиска.
     */
    fun onQueryChange(newQuery: String) {
        viewModelScope.launch {
            _query.value = newQuery
        }
    }
}