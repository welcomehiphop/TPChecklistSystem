package com.samsung.thai.connectiondatabase.activitys

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.samsung.thai.connectiondatabase.LoadingDialog
import com.samsung.thai.connectiondatabase.R

class ScannerActivity : AppCompatActivity() {
    lateinit var txtEmp : TextView
    lateinit var txtGetDate : TextView
    lateinit var txtWeek : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        if(resources.getBoolean(R.bool.portrait_only)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        txtEmp = findViewById(R.id.txtEmp)
        val button = findViewById<Button>(R.id.button)
        val btn_edit = findViewById<Button>(R.id.btn_edit)
        txtGetDate = findViewById(R.id.txtGetDate)
        txtWeek = findViewById(R.id.txtWeek)
        var dateChange: String = dbConnect2.getDate()
        if(dateChange.isNotEmpty()){
            dateChange = dateChange.substring(8)  + "/" +dateChange.substring(5,7) + "/" + dateChange.substring(0,4)
        }
        val empID = intent.getStringExtra("empID")
        txtWeek.text = intent.getStringExtra("week")
        txtGetDate.text = dateChange
        txtEmp.text = "Emp ID : $empID"

        button.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            IntentIntegrator(this).apply {
                captureActivity = CustomActivity::class.java
                setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                setCameraId(0)
                setBeepEnabled(false)
                setOrientationLocked(false)
                initiateScan()
            }
        }
        btn_edit.setOnClickListener {
            val loadingDialog = LoadingDialog(this@ScannerActivity)
            loadingDialog.startLoadingDialog()
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("date",dateChange)
                intent.putExtra("empID",empID)
                startActivity(intent)
            },1000)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: ", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, ChecklistActivity::class.java)
                    intent.putExtra("week",txtWeek.text)
                    intent.putExtra("qrCode",result.contents)
                    intent.putExtra("date",txtGetDate.text)
                    intent.putExtra("empID",txtEmp.text)
                    startActivity(intent)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}