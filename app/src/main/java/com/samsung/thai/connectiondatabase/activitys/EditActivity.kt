package com.samsung.thai.connectiondatabase.activitys

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.samsung.thai.connectiondatabase.CustomDropDownAdapter
import com.samsung.thai.connectiondatabase.R
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2
import com.samsung.thai.connectiondatabase.EditAdater


class EditActivity : AppCompatActivity(), EditAdater.OnItemClickListener{
    companion object{
        var dbConnect2: dbConnect2 = dbConnect2()
    }

    lateinit var rvEdit : RecyclerView
    var ngList  = dbConnect2.getNGList("select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status from TP_CheckResult as a join TP_ContentCheck as b on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c on a.machine_id = c.machine_id where YEAR(before_reg_date) = YEAR(getdate())")
    var adapterList = EditAdater(ngList, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        if(resources.getBoolean(R.bool.portrait_only)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        rvEdit = findViewById(R.id.rv_edit)
        val layoutManager = LinearLayoutManager(applicationContext)
        rvEdit.layoutManager = layoutManager

        val empNo = intent.getStringExtra("empID").toString()
        ngList =  dbConnect2.getNGList("select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status from TP_CheckResult as a join TP_ContentCheck as b on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c on a.machine_id = c.machine_id where before_empno = '$empNo' and YEAR(before_reg_date) = YEAR(getdate())")

    }

    override fun onStart() {
        super.onStart()

        val empNo = intent.getStringExtra("empID").toString()
        ngList =  dbConnect2.getNGList("select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status from TP_CheckResult as a join TP_ContentCheck as b on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c on a.machine_id = c.machine_id where before_empno = '$empNo' and YEAR(before_reg_date) = YEAR(getdate())")
        adapterList = EditAdater(ngList, this)
        rvEdit.adapter = adapterList
        val tvEmpNo = findViewById<TextView>(R.id.tv_EmpNo)

        val allCount = findViewById<TextView>(R.id.allCount)
        allCount.text = dbConnect2.getCountAll(empNo)
        val pendingCount = findViewById<TextView>(R.id.pendingCount)
        pendingCount.text = dbConnect2.getCount(empNo,"Pending")
        val finishCount = findViewById<TextView>(R.id.finishCount)
        finishCount.text = dbConnect2.getCount(empNo,"Finish")
//        allCount.setTextColor(Color.parseColor("#bdbdbd"))
        tvEmpNo.text = "EmpNo : $empNo"
        val filterData = resources.getStringArray(R.array.filterData)
        val spinner = findViewById<Spinner>(R.id.spinnerFilter)

        if (spinner != null) {
//            val adapter = ArrayAdapter(this, R.layout.spinner_item2, filterData)
            val status : ArrayList<String> = ArrayList(listOf(*resources.getStringArray(R.array.filterData)))
            val customDropDownAdapter = CustomDropDownAdapter(this, status)
            spinner.adapter = customDropDownAdapter
            spinner.setSelection(1)

            spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View?, position: Int, id: Long) {
                    when {
                        filterData[position] == "All" -> {
                            hideSoftKeyboard(this@EditActivity)
                            ngList = dbConnect2.getNGList("select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status " +
                                    "from TP_CheckResult as a join TP_ContentCheck as b " +
                                    "on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c " +
                                    "on a.machine_id = c.machine_id " +
                                    "where before_empno = '$empNo' and YEAR(before_reg_date) = YEAR(getdate())")
                            adapterList = EditAdater(ngList, this@EditActivity)
                            rvEdit.adapter = adapterList
                        }
                        filterData[position] == "Pending" -> {
                            hideSoftKeyboard(this@EditActivity)
                            ngList = dbConnect2.getNGList("select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status " +
                                    "from TP_CheckResult as a join TP_ContentCheck as b " +
                                    "on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c " +
                                    "on a.machine_id = c.machine_id " +
                                    "where before_empno = '$empNo' and check_status = 'Pending' and YEAR(before_reg_date) = YEAR(getdate())")
                            adapterList = EditAdater(ngList, this@EditActivity)
                            rvEdit.adapter = adapterList
                        }
                        filterData[position] == "Finish" -> {
                            hideSoftKeyboard(this@EditActivity)
                            ngList = dbConnect2.getNGList("select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status " +
                                    "from TP_CheckResult as a join TP_ContentCheck as b " +
                                    "on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c " +
                                    "on a.machine_id = c.machine_id " +
                                    "where before_empno = '$empNo' and check_status = 'Finish' and YEAR(before_reg_date) = YEAR(getdate())")
                            adapterList = EditAdater(ngList, this@EditActivity)
                            rvEdit.adapter = adapterList
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        }

//        val filterDataItem = resources.getStringArray(R.array.filterDataItem)
//        val spinnerItem = findViewById<Spinner>(R.id.spinnerFilterItem)
//        if (spinnerItem != null) {
//            val adapter = ArrayAdapter(this, R.layout.spinner_item2, filterDataItem)
//            spinnerItem.adapter = adapter
//            spinnerItem.onItemSelectedListener = object :
//                    AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>,
//                                            view: View?, position: Int, id: Long) {
//                    if(filterDataItem[position] != "Select Item"){
//                        ngList = dbConnect2.getNGList("select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status from TP_CheckResult as a join TP_ContentCheck as b on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c on a.machine_id = c.machine_id join TP_CheckList as d on a.check_id = d.check_id where d.check_list like '%${filterDataItem[position]}%'")
//                        adapterList = EditAdater(ngList, this@EditActivity)
//                        rvEdit.adapter = adapterList
//                        Toast.makeText(this@EditActivity,filterDataItem[position],Toast.LENGTH_LONG).show()
//                    }
//
//                }
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                }
//            }
//
//        }
    }


    override fun onItemClick(position: Int) {
        if(ngList[position].checkStatus == "Pending"){
            val empID = intent.getStringExtra("empID").toString()
            val intent = Intent(this, NGActivity::class.java)
            intent.putExtra("empID", empID)
            intent.putExtra("plant", ngList[position].plant)
            intent.putExtra("check_id", ngList[position].check_id)
            intent.putExtra("machine_id", ngList[position].machine_id)
            intent.putExtra("content_id", ngList[position].content_id)
            intent.putExtra("week", ngList[position].week)
            intent.putExtra("date", ngList[position].beforeDate)
            intent.putExtra("contentName", ngList[position].contentName)
            startActivity(intent)
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