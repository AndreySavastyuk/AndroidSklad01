package com.example.warehouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.warehouse.data.PickDao

/**
 * Фабрика для создания PickLogViewModel.
 */
class PickLogViewModelFactory(
    private val dao: PickDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PickLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PickLogViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}