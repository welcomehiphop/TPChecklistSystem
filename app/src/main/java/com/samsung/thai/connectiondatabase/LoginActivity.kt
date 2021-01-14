package com.samsung.thai.connectiondatabase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val button = findViewById<Button>(R.id.btn_scan)
        val etEmp = findViewById<EditText>(R.id.et_emp)
        val txtGetDate = findViewById<TextView>(R.id.txtGetDate)
        val txtWeek = findViewById<TextView>(R.id.txtWeek)
        val dbConnect2 = dbConnect2()
        // get date from database
        var dateChange: String = dbConnect2.getDate()
        dateChange = dateChange.substring(8)  + "/" +dateChange.substring(5,7) + "/" + dateChange.substring(0,4)
        // show Week number and Date
        txtWeek.text = "Week : " + dbConnect2.getWeek()
        txtGetDate.text = "Date : $dateChange"
        button.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            hideSoftKeyboard(this)
            if(etEmp.text.isNotBlank()){
                val intent = Intent(this,ScannerActivity::class.java)
//                button.text = etEmp.text
                intent.putExtra("week",txtWeek.text)
                intent.putExtra("date",txtGetDate.text)
                intent.putExtra("empID",etEmp.text.toString())
                startActivity(intent)
            }else{
                Toast.makeText(this,"Please enter your employee ID.", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
                INPUT_METHOD_SERVICE) as InputMethodManager
        if(activity.currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0)
        }
    }
}