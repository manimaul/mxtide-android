package com.mxmariner.tides.model

import android.app.Activity
import android.content.Intent

data class ActivityResult(
        val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
) {

    val isResultOk
        get() = resultCode == Activity.RESULT_OK
}
