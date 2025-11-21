package com.bytedance.trainingcamp.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {

    private var toast: Toast? = null

    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        toast = Toast.makeText(context, message, duration)
        toast?.show()
    }

    fun showLong(context: Context, message: String) {
        show(context, message, Toast.LENGTH_LONG)
    }

    fun cancel() {
        toast?.cancel()
    }
}
