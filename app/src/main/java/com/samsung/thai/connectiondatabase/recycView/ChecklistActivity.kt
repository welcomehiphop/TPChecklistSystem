package com.samsung.thai.connectiondatabase.recycView


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.samsung.thai.connectiondatabase.DetailActivity
import com.samsung.thai.connectiondatabase.R
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2


class ChecklistActivity : AppCompatActivity(),ExampleAdater.OnItemClickListener{
    companion object{
        var dbConnect2: dbConnect2 = dbConnect2()
    }
    var themPatrol = dbConnect2.getThemePatrol(dbConnect2.getWeek().toInt())
    lateinit var rvList : RecyclerView
    lateinit var txtDayName : TextView
    lateinit var txtDate : TextView
    lateinit var empID : String
    lateinit var qrCode : String
    lateinit var txtPlant : TextView
    lateinit var txtPatrol : TextView
    lateinit var adapterList : ExampleAdater
    lateinit var machineID : String
    lateinit var checkList : ArrayList<CheckList>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        txtPatrol = findViewById(R.id.txtPatrol)
        txtPatrol.text = themPatrol[1]

        val txtMachineName = findViewById<TextView>(R.id.txtMachineName)
        txtPlant = findViewById(R.id.txtPlant)
        val txtLine = findViewById<TextView>(R.id.txtLine)
        rvList = findViewById(R.id.rvProduct)
        empID = intent.getStringExtra("empID").toString()
        qrCode = intent.getStringExtra("qrCode").toString()
        machineID = qrCode
        val machineInfo = dbConnect2.getMachineName(machineID)
        if(machineInfo.isNullOrEmpty()){
            txtMachineName.text = "Machine Name : " + "Unknown"
            txtPlant.text = "Plant : " + "Unknown"
            txtLine.text = "Line : " + "Unknown"
            Toast.makeText(this,"Unknown Machine",Toast.LENGTH_LONG).show()
        }else{
            txtMachineName.text = "Machine Name : " + machineInfo[0]
            txtPlant.text = "Plant : " + machineInfo[1]
            txtLine.text = "Line : " + machineInfo[2]
        }

        checkList = dbConnect2.getCheckList(dbConnect2.getWeek().toInt(),machineID)
        val layoutManager = LinearLayoutManager(applicationContext)
        rvList.layoutManager = layoutManager
        adapterList = ExampleAdater(checkList, this)
        rvList.adapter = adapterList


        txtDayName = findViewById(R.id.txtDayName)
        txtDate = findViewById(R.id.txtDate)
        txtDayName.text = intent.getStringExtra("week")
        txtDate.text = intent.getStringExtra("date")

        val datePicker = findViewById<ImageView>(R.id.date_picker)
        datePicker.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            numberPickerCustom2(this)
//        }
        }
    }

    override fun onItemClick(position: Int) {

        if(txtDayName.text.toString().substring(7) == dbConnect2.getWeek() && checkList[position].Status != "Finish" && checkList[position].Status != "Pending"){
            adapterList.notifyDataSetChanged()
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("checkID",themPatrol[0])
            intent.putExtra("contentID",checkList[position].content_id)
            intent.putExtra("week", txtDayName.text)
            intent.putExtra("qrCode", qrCode)
            intent.putExtra("itemList", checkList[position].List)
            intent.putExtra("date", txtDate.text)
            intent.putExtra("empID", empID)
            intent.putExtra("plant", txtPlant.text)
            intent.putExtra("position",position)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val status = data!!.getStringExtra("status").toString()
                val position = data!!.getIntExtra("position2",0)
                checkList[position].Status = status
                adapterList.notifyDataSetChanged()
            }
        }
    }

    private fun numberPickerCustom2(context: Context) {
        val d = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.number_picker_dialog, null)
        d.setTitle("Choose Week")
        d.setMessage("Select Week")
        d.setView(dialogView)
        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.dialog_number_picker)
        numberPicker.maxValue = 52
        numberPicker.minValue = 1
        numberPicker.wrapSelectorWheel = false
        numberPicker.value = dbConnect2.getWeek().toInt()
        numberPicker.setOnValueChangedListener { _, _, _ -> println("onValueChange: ") }
        d.setPositiveButton("Done") { _, _ ->
            txtDayName.text = "Week : " + numberPicker.value
            checkList = dbConnect2.getCheckList(numberPicker.value, machineID)
            themPatrol = dbConnect2.getThemePatrol(numberPicker.value)
            txtPatrol.text = themPatrol[1]
            val adapterList = ExampleAdater(checkList, this)
            rvList.adapter = adapterList
        }
        d.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog = d.create()
        alertDialog.show()
    }
}