package com.samsung.thai.connectiondatabase

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

class LoadingDialog (private val context: Context) {


    private lateinit var dialog : AlertDialog

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()

        dialog = builder.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}