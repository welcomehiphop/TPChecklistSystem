package com.samsung.thai.connectiondatabase.activitys

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.samsung.thai.connectiondatabase.API.RetrofitClient
import com.samsung.thai.connectiondatabase.LoadingDialog
import com.samsung.thai.connectiondatabase.Models.LoginResponse
import com.samsung.thai.connectiondatabase.R
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(resources.getBoolean(R.bool.portrait_only)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        val button = findViewById<Button>(R.id.btn_scan)
        val etEmp = findViewById<EditText>(R.id.et_emp)
        val txtGetDate = findViewById<TextView>(R.id.txtGetDate)
        val txtWeek = findViewById<TextView>(R.id.txtWeek)
        val dbConnect2 = dbConnect2()
        // get date from database
        var dateChange: String = dbConnect2.getDate()
        if(dateChange.isNotEmpty()){
            dateChange = dateChange.substring(8) + "/" + dateChange.substring(5, 7) + "/" + dateChange.substring(0, 4)
        }
        // show Week number and Date
        txtWeek.text = "Week : " + dbConnect2.getWeek()
        txtGetDate.text = "Date : $dateChange"

        button.setOnClickListener {

            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            hideSoftKeyboard(this)
            val empNo = etEmp.text.toString().trim()
            RetrofitClient.instance.userLogin(empNo)
                    .enqueue(object : Callback<List<LoginResponse>> {
                        override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<List<LoginResponse>>, response: Response<List<LoginResponse>>) {
                            if (response.body()!!.isNotEmpty()) {
                                    val loadingDialog = LoadingDialog(this@LoginActivity)

                                    loadingDialog.startLoadingDialog()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        if (empNo == response.body()!![0].usrid) {
                                            val intent = Intent(applicationContext, ScannerActivity::class.java)
                                            intent.putExtra("week", txtWeek.text)
                                            intent.putExtra("date", txtGetDate.text)
                                            intent.putExtra("empID", empNo)
                                            startActivity(intent)
                                        }
                                        loadingDialog.dismissDialog()
                                    },2000)

                            } else {
                                Toast.makeText(applicationContext, "EmpNo Incorrect", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
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

