package com.dydrian.mob22.core

import android.content.Context
import android.widget.Toast

fun String.crop(charCount: Int): String {
    if (this.length <= charCount) {
        return this
    }
    return this.take(charCount) + "..."
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
