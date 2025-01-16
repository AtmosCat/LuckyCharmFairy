package com.luckycharmfairy.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.luckycharmfairy.luckycharmfairy.R

object SpinnerUtils {
    fun setSpinnerAdapter(spinner: Spinner, context: Context, items: List<String>) {
        val adapter = ArrayAdapter(context, R.layout.custom_spinner_item, items).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter
    }
}
