package com.example.warehouse.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.warehouse.data.TaskDao

/**
 * Factory для ReceiveViewModel — теперь принимает и DAO, и Context
 */
class ReceiveViewModelFactory(
    private val dao: TaskDao,
    private val printerMac: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReceiveViewModel(dao, printerMac) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}