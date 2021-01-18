package com.samsung.thai.connectiondatabase

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.samsung.thai.connectiondatabase.Models.CheckList
import com.samsung.thai.connectiondatabase.Models.NGList

class ExampleAdater(private val checkList: List<CheckList>,
                    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ExampleAdater.ExampleViewHolder>(){
    var mLastClickTime = System.currentTimeMillis()
    var CLICK_TIME_INTERVAL = 500
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.io_products, parent, false)
        return ExampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val checkList: CheckList = checkList[position]
        holder.txtCheckList.text = checkList.content_id + ". " +  checkList.List
        holder.txtCheckStatus.text = checkList.Status
    }

    override fun getItemCount() = checkList.size

    inner class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener{
        val txtCheckList: TextView = itemView.findViewById(R.id.txtCheckList)
        val txtCheckStatus: TextView = itemView.findViewById(R.id.txtCheckStatus)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {

            val now  = System.currentTimeMillis()
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return
            }
            Log.d("TestDoubleClick",(now - mLastClickTime).toString())
            mLastClickTime = now
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}

class EditAdater(private val ngList: List<NGList>,
                 private val listener: OnItemClickListener
) : RecyclerView.Adapter<EditAdater.EditViewHolder>(){
    var mLastClickTime = System.currentTimeMillis()
    var CLICK_TIME_INTERVAL = 500
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.io_edit, parent, false)
        return EditViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
        val ngList: NGList = ngList[position]
//        holder.txtNo.text = (position + 1).toString()
        holder.txtWeek.text = ngList.week
//        holder.txtPlant.text = ngList.plant
        holder.txtLine.text = ngList.line
        holder.txtMachineName.text = ngList.machineName
        holder.txtContentCheck.text = ngList.contentName
        holder.txtNGDate.text =  ngList.beforeDate.substring(8,10) + "/" +  ngList.beforeDate.substring(5,7) + "/" + ngList.beforeDate.substring(0,4) + ngList.beforeDate.substring(10)
        holder.txtCheckStatus.text = ngList.checkStatus
        if(holder.txtCheckStatus.text == "Finish"){
            holder.txtCheckStatus.setTextColor(Color.parseColor("#0000FF"))
        }else{
            holder.txtCheckStatus.setTextColor(Color.parseColor("#FF0000"))
        }
    }

    override fun getItemCount() = ngList.size

    inner class EditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener{
//        val txtNo : TextView = itemView.findViewById(R.id.txtNo)
        val txtWeek : TextView = itemView.findViewById(R.id.txt_Week)
//        val txtPlant : TextView = itemView.findViewById(R.id.txt_Plant)
        val txtLine : TextView = itemView.findViewById(R.id.txt_Line)
        val txtMachineName : TextView = itemView.findViewById(R.id.txt_Machine_Name)
        val txtContentCheck : TextView = itemView.findViewById(R.id.txt_Content_check)
        val txtNGDate : TextView = itemView.findViewById(R.id.txt_NG_DATE)
        val txtCheckStatus : TextView = itemView.findViewById(R.id.txt_Check_Status)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val now  = System.currentTimeMillis()
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return
            }
            mLastClickTime = now
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}



