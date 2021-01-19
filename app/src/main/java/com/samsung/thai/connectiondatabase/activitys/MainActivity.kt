package com.samsung.thai.connectiondatabase.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.samsung.thai.connectiondatabase.R
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}