package com.samsung.thai.connectiondatabase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2
import com.samsung.thai.connectiondatabase.recycView.*

class MainActivity : AppCompatActivity(){
    var db = dbConnect2()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun CustomerOnClick(view: View){
        val intent = Intent(this, ChecklistActivity::class .java)
        startActivity(intent)
    }



}