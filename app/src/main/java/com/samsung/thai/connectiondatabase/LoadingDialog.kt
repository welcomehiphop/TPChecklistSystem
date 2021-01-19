package com.samsung.thai.connectiondatabase

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

class LoadingDialog (private val context: Context) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var dialog : AlertDialog

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog = builder.show()

    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}