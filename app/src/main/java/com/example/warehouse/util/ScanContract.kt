package com.example.warehouse.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.ScanOptions

class ScanContract : ActivityResultContract<ScanContract.Options, IntentResult>() {
    data class Options(
        var prompt: String = "",
        var beepEnabled: Boolean = false
    )

    override fun createIntent(
        context: Context,
        input: Options
    ): Intent {
        return IntentIntegrator(context as Activity)
            .setPrompt(input.prompt)
            .setBeepEnabled(input.beepEnabled)
            .createScanIntent()
    }

    override fun parseResult(
        resultCode: Int, intent: Intent?
    ): IntentResult {
        return IntentIntegrator.parseActivityResult(resultCode, intent)
    }
}