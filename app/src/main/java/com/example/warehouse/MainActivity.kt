package com.example.warehouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.warehouse.screens.MainScreen
import com.example.warehouse.screens.ReceiveScreen
import com.example.warehouse.screens.PickScreen
import com.example.warehouse.screens.SettingsScreen
import com.example.warehouse.screens.PrinterConfig
import com.example.warehouse.ui.theme.WarehouseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WarehouseTheme {
                val navController = rememberNavController()

                // Состояние принтеров и настроек
                var printers by remember {
                    mutableStateOf(
                        listOf(
                            PrinterConfig("Xprinter V3 BT", "10:23:81:5B:DA:29")
                        )
                    )
                }
                var selectedPrinterIndex by rememberSaveable { mutableStateOf(0) }
                var printEnabled by rememberSaveable { mutableStateOf(true) }

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(
                            onReceive = { navController.navigate("receive") },
                            onPick    = { navController.navigate("pick") },
                            onSettings= { navController.navigate("settings") },
                            onHistory = { /* TODO: history nav */ }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            initialPrinters = printers,
                            initialSelected = selectedPrinterIndex,
                            initialEnabled  = printEnabled,
                            onSave = { newPrinters, newIndex, newEnabled ->
                                printers = newPrinters
                                selectedPrinterIndex = newIndex
                                printEnabled = newEnabled
                                navController.popBackStack()
                            },
                            onCancel = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("receive") {
                        // Передаём MAC текущего принтера и флаг печати
                        val mac = printers.getOrNull(selectedPrinterIndex)?.mac.orEmpty()
                        ReceiveScreen(mac = mac, printEnabled = printEnabled)
                    }

                    composable("pick") {
                        val mac = printers.getOrNull(selectedPrinterIndex)?.mac.orEmpty()
                        PickScreen(mac = mac)
                    }
                }
            }
        }
    }
}
