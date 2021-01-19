package com.samsung.thai.connectiondatabase.dbHelper

import android.os.StrictMode
import com.samsung.thai.connectiondatabase.Models.CheckList
import com.samsung.thai.connectiondatabase.Models.NGList
import java.sql.*
import kotlin.collections.ArrayList

class dbConnect2 {

    lateinit var connection: Connection
    lateinit var resultSet: ResultSet
    lateinit var statement: Statement

    var cn_81_9:String = "jdbc:jtds:sqlserver://192.168.7.114;databaseName=intra_TSE;user=sa;password=1234"
//    var cn_81_9:String = "jdbc:jtds:sqlserver://107.101.81.9:1433;databaseName=intra_TSE;user=sa;password=tsePortal@2013"

    fun updateNG(pic_after: String,after_empno: String,after_reg_date: String,after_comment: String,check_id: String,machine_id: String,content_id: String,week: String){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            statement.executeUpdate("UPDATE TP_CheckResult SET picture_after = '$pic_after',check_status = 'Finish',after_empno = '$after_empno',after_reg_date = '$after_reg_date',after_comment = N'$after_comment' WHERE check_id = '$check_id' and machine_id = '$machine_id' and content_id = '$content_id' and week_number = '$week'")
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getCountAll(empID:String) : String{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var count = ""

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("select COUNT(check_status) as allCount from TP_CheckResult where before_empno = '$empID' and YEAR(before_reg_date) = YEAR(getdate())")
            while (resultSet.next()) {
                count = resultSet.getString("allCount").toString()
            }
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return count
    }
    fun getCount(empID:String , condition: String) : String{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var count = ""
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("select COUNT(check_status) as allCount from TP_CheckResult where before_empno = '$empID' and check_status = '$condition' and YEAR(before_reg_date) = YEAR(getdate())")
            while (resultSet.next()) {
                count = resultSet.getString("allCount").toString()
            }
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return count
    }

    fun getNGList(str:String) : ArrayList<NGList>{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val ngList = ArrayList<NGList>()
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
//            val str = "select c.plant,a.check_id,a.machine_id,a.content_id,a.week_number,c.plant,c.line,c.machine_name,content_name,a.before_empno,a.before_reg_date,check_status from TP_CheckResult as a join TP_ContentCheck as b on a.check_id = b.check_id and a.content_id = b.content_id join TP_Machine as c on a.machine_id = c.machine_id where check_status = 'Pending'"
            resultSet = statement.executeQuery(str)
            while (resultSet.next()) {
                val check_id  = resultSet.getString("check_id")
                val machine_id  = resultSet.getString("machine_id")
                val content_id  = resultSet.getString("content_id")
                val week  = resultSet.getString("week_number")
                val plant = resultSet.getString("plant")
                val line = resultSet.getString("line")
                val machineName = resultSet.getString("machine_name")
                val contentName = resultSet.getString("content_name")
                val beforeEmpNo = resultSet.getString("before_empno")
                val beforeRegDate=  resultSet.getString("before_reg_date")
                var status = resultSet.getString("check_status")
                if(status == null){
                    status = "No"
                }
                ngList.add(NGList(check_id,machine_id,content_id,week,plant,line,machineName,contentName,beforeEmpNo,beforeRegDate,status))
            }
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return ngList
    }

    fun getCheckList(week:Int,machine_id: String,empID: String): ArrayList<CheckList>{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val checkList = ArrayList<CheckList>()
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            val str = "select *,(select check_status from TP_CheckResult where check_id = t_all.check_id and content_id = t_all.content_id and machine_id = t_all.machine_id and before_empno = '$empID' and YEAR(before_reg_date) = YEAR(GETDATE())) as check_status from (select d.machine_id,e.machine_name,b.check_id,a.content_name,a.content_id from TP_ContentCheck as a join TP_WeekCheck as b on a.check_id = b.check_id join TP_CheckList as c on a.check_id = b.check_id join TP_CheckMachine as d on c.check_machine = d.check_machine join TP_Machine as e on d.machine_id = e.machine_id where b.week = '$week' and d.machine_id = '$machine_id' group by a.content_id,b.check_id,a.content_name,d.machine_id,e.machine_name) as t_all"
//            val str = "select content_id,content_check from content_check join CheckList2 on content_check.check_id = CheckList2.check_id where CheckList2.week = '$week'"
            resultSet = statement.executeQuery(str)
            while (resultSet.next()) {
                var status = resultSet.getString("check_status")
                val listItem = resultSet.getString("content_name")
                val contentID = resultSet.getString("content_id")
                if(status == null){
                    status = "No"
                }
                checkList.add(CheckList(contentID,listItem,status))
            }
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return checkList
    }


    fun getMachineName(id: String) : ArrayList<String>{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val machine = ArrayList<String>()
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("select plant,line,machine_name from TP_Machine where machine_id = '$id'")
            while (resultSet.next()) {
                val manchineName = resultSet.getString("machine_name").toString()
                val plantName = resultSet.getString("plant").toString()
                val lineName = resultSet.getString("line").toString()
                machine.add(manchineName)
                machine.add(plantName)
                machine.add(lineName)

            }
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return machine
    }
    fun getThemePatrol(week:Int) : ArrayList<String> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var themePatrol = ArrayList<String>()
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("select TP_CheckList.check_id,TP_CheckList.check_list from TP_CheckList join TP_WeekCheck on TP_CheckList.check_id = TP_WeekCheck.check_id where TP_WeekCheck.week = '$week'")
            while (resultSet.next()) {
                val check_id = resultSet.getString("check_id").toString()
                val check_list = resultSet.getString("check_list").toString()
                themePatrol.add(check_id)
                themePatrol.add(check_list)
            }
            connection.close()
        }catch (e: ClassNotFoundException){
            e.printStackTrace()
        }catch (e: SQLException){
            e.printStackTrace()
        }
        return themePatrol
    }
//
    fun addResult(check_id:String,week: String,machine_id: String,content_id: String,check_status: String,comment: String,pic_before: String,pic_after: String,before_empno: String,before_reg_date : String,after_empno:String,after_reg_date:String) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("INSERT INTO TP_CheckResult (check_id,week_number,machine_id,content_id,check_status,before_comment,picture_before,picture_after,before_empno,before_reg_date,after_empno,after_reg_date) VALUES ('$check_id','$week', '$machine_id', '$content_id', '$check_status',N'$comment','$pic_before','$pic_after','$before_empno','$before_reg_date','$after_empno','$after_reg_date');")
//            resultSet = statement.executeQuery("INSERT INTO CheckListResult (week_number,detail,check_status,comment,picuture_before,picture_after,machine_id,emp_id,check_date) VALUES ('week', 'machine_id', 'detail', 'check_status','comment','pic_before','pic_after','empID','date');")
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun getImageUrl(check_id: String,machine_id: String,content_id: String,week: String) : String{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var imageUrl = ""
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("select picture_before from TP_CheckResult where check_id = '$check_id' and machine_id = '$machine_id' and content_id = '$content_id' and week_number = '$week'")
            while (resultSet.next()) {
                imageUrl = resultSet.getString("picture_before").toString()
            }
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return imageUrl
    }

    fun getDate() : String{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var date = ""

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("SELECT CAST( GETDATE() AS Date )")
            while (resultSet.next()) {
                date = resultSet.getString(1).toString()
            }
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return date
    }
    fun getDateAndTime() : String{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var date = ""
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("SELECT GETDATE();")
            while (resultSet.next()) {
                date = resultSet.getString(1).toString()
            }
            date = date.replace("\\s".toRegex(),"")
            date = date.replace(":".toRegex(),"-")
            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return date
    }


    fun getWeek() : String{
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var week : String = ""
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connection = DriverManager.getConnection(cn_81_9)
            statement = connection.createStatement()
            resultSet = statement.executeQuery("set datefirst 1;select datepart(week, '${getDate()}');")
            while (resultSet.next()) {
                week = resultSet.getString(1).toString()
            }

            connection.close()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return week
    }

//    fun getCheckList(day:String): ArrayList<CheckList>{
//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        StrictMode.setThreadPolicy(policy)
//        val checkList = ArrayList<CheckList>()
//        try {
//            Class.forName("net.sourceforge.jtds.jdbc.Driver")
//            connection = DriverManager.getConnection(cn_81_9)
//            statement = connection.createStatement()
//            resultSet = statement.executeQuery("select list_item from TestCheckList join TestData on TestData.customer_id = TestCheckList.customer_id where date_name = '$day'")
//            while (resultSet.next() == true) {
////                val dateName = resultSet.getString("date_name").toString()
////                val customerID = resultSet.getString("customer_id").toString()
////                val customerName = resultSet.getString("customer_name").toString()
////                customer.add(Customer(customerID, customerName, dateName))
//                val listItem = resultSet.getString("list_item").toString()
//                checkList.add(CheckList(listItem,"No"))
//            }
//            connection.close()
//        } catch (e: ClassNotFoundException) {
//            e.printStackTrace()
//        } catch (e: SQLException) {
//            e.printStackTrace()
//        }
//        return checkList
//    }

//    fun getCheckList(day:String): ArrayList<Customer> {
//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        StrictMode.setThreadPolicy(policy)
//        val customer = ArrayList<Customer>()
//        try {
//
//            Class.forName("net.sourceforge.jtds.jdbc.Driver")
//            connection = DriverManager.getConnection(cn_81_9)
//            statement = connection.createStatement()
//            resultSet = statement.executeQuery("select customer_id,customer_name,date_name from TestData where date_name = '$day'")
//            while (resultSet.next() == true) {
//                val dateName = resultSet.getString("date_name").toString()
//                val customerID = resultSet.getString("customer_id").toString()
//                val customerName = resultSet.getString("customer_name").toString()
//                customer.add(Customer(customerID, customerName, dateName))
//            }
//            connection.close()
//        } catch (e: ClassNotFoundException) {
//            e.printStackTrace()
//        } catch (e: SQLException) {
//            e.printStackTrace()
//        }
//        return customer
//    }

//    fun getProductList(): ArrayList<Product> {
//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        StrictMode.setThreadPolicy(policy)
//        val product = ArrayList<Product>()
//        try {
//            Class.forName("net.sourceforge.jtds.jdbc.Driver")
//            connection = DriverManager.getConnection(cn_81_9)
//            statement = connection.createStatement()
//            resultSet = statement.executeQuery("select product_id,product_name from TestProduct")
//
//            while (resultSet.next() == true) {
//                val productID = resultSet.getString("product_id").toString()
//                val productName = resultSet.getString("product_name").toString()
//                product.add(Product(productID,productName))
//            }
//            connection.close()
//        } catch (e: ClassNotFoundException) {
//            e.printStackTrace()
//        } catch (e: SQLException) {
//            e.printStackTrace()
//        }
//        return product
//    }
}