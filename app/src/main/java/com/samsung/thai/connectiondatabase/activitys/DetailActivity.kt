package com.samsung.thai.connectiondatabase.activitys

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.samsung.thai.connectiondatabase.*
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.isNotEmpty
import kotlin.collections.set


private const val PERMISSION_CODE_READ = 1001
private const val PERMISSION_CODE_WRITE = 1002
private const val IMAGE_PICK_CODE = 1
private const val PERMISSION_CODE = 1000
private const val IMAGE_CAPTURE_CODE = 0
private var image_uri: Uri? = null
private var isCameraOne = true
private var imageData: ByteArray? = null
private var imageData2: ByteArray? = null
private const val postURL: String = "http://107.101.81.9:11111/machine_audit/upload_image.php" //
val dbConnect2 = dbConnect2()
var imageName = ""
var imageBefore =""
var imageAfter = ""

class DetailActivity : AppCompatActivity() {

    var bitmapTemp: Bitmap? = null
    var bitmapTemp2: Bitmap? = null
    lateinit var imageView: ImageView
    lateinit var imageView2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        if(resources.getBoolean(R.bool.portrait_only)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        val txtDayName = findViewById<TextView>(R.id.txtDayName)
        val txtDate = findViewById<TextView>(R.id.txtDate)
        val txtDetail = findViewById<TextView>(R.id.txtDetail)
        val spinner = findViewById<Spinner>(R.id.spinner_status)
        val etComment = findViewById<EditText>(R.id.etComment)

        txtDetail.text = "Content Check : " + intent.getStringExtra("itemList")
        txtDate.text = intent.getStringExtra("date")
        txtDayName.text = intent.getStringExtra("week")

        if (spinner != null) {
            val status : ArrayList<String> = ArrayList(listOf(*resources.getStringArray(R.array.CheckStatus)))
            val customDropDownAdapter = CustomDropDownAdapter(this, status)
//            val adapter = ArrayAdapter(this, R.layout.spinner_item, checkStatus)
            spinner.adapter = customDropDownAdapter
            spinner.setSelection(0)
            spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

        }

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            val loadingDialog = LoadingDialog(this@DetailActivity)
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 1000) }
            val position = intent.getIntExtra("position", 0)
            Log.i("Testaaa", position.toString())
            val checkID = intent.getStringExtra("checkID").toString()
            val week = intent.getStringExtra("week").toString()
            val machineID = intent.getStringExtra("qrCode").toString()
            val contentID = intent.getStringExtra("contentID").toString()
            val status = spinner.selectedItem.toString()
            val comment = etComment.text.toString()
            val empID = intent.getStringExtra("empID").toString()
            Log.d("EMPID", "EMPID=$empID")
            val plant = intent.getStringExtra("plant").toString()
            var date = dbConnect2.getDate()

            var dateAndTime = dbConnect2.getDateAndTime()
            val inputdate = date.substring(0,4) + "-" + date.substring(5, 7) + "-" + date.substring(8) + " " +  dateAndTime.substring(10,12) + ":" + dateAndTime.substring(13,15) + ":" + dateAndTime.substring(16,18)
            var imageLink = "http://107.101.81.9:11111/machine_audit/img_mobile/" +
                    "${plant.substring(8)}/" +
                    "${date.substring(0, 4)}/" +
                    "${date.substring(5, 7)}/" +
                    "${date!!.substring(8)}/" +
                    "$imageBefore"
            var imageLink2 = "http://107.101.81.9:11111/machine_audit/img_mobile/" +
                    "${plant!!.substring(8)}/" +
                    "${date.substring(0, 4)}/" +
                    "${date.substring(5, 7)}/" +
                    "${date.substring(8)}/" +
                    "$imageAfter"
            if (bitmapTemp != null && bitmapTemp2 != null) {
                if(status == "Finish"){
                    btnSubmit.isClickable = false
                    uploadImage(bitmapTemp!!, imageBefore)
                    uploadImage(bitmapTemp2!!, imageAfter)
                    dbConnect2.addResult(
                            checkID,
                            week.substring(7),
                            machineID,
                            contentID,
                            status,
                            comment,
                            imageLink,
                            imageLink2,
                            empID.substring(9),
                            inputdate,
                            empID.substring(9),
                            inputdate
                    )

                    loadingDialog.startLoadingDialog()
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        val intent = Intent()
                        intent.putExtra("status","Finish")
                        intent.putExtra("position2",position)
                        setResult(RESULT_OK,intent)
                        finish()
                        loadingDialog.dismissDialog()
                    }, 1000)
                }else {
                    Toast.makeText(this,"Please change status.",Toast.LENGTH_LONG).show()
                }
            }else if (bitmapTemp != null && bitmapTemp2 == null) {
                if(status == "Pending"){
                    btnSubmit.isClickable = false
                    uploadImage(bitmapTemp!!, imageBefore)
                    imageLink2 = ""
                    dbConnect2.addResult(
                            checkID,
                            week.substring(7),
                            machineID,
                            contentID,
                            status,
                            comment,
                            imageLink,
                            imageLink2,
                            empID.substring(9),
                            inputdate,
                            "",
                            ""
                    )
                    loadingDialog.startLoadingDialog()
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        val intent = Intent()
                        intent.putExtra("status","Pending")
                        intent.putExtra("position2",position)
                        setResult(RESULT_OK,intent)
                        finish()
                        loadingDialog.dismissDialog()
                    }, 1000)
                }else{
                    Toast.makeText(this,"Please change status.",Toast.LENGTH_LONG).show()
                }
            }else {
                Toast.makeText(this, "Please select image.", Toast.LENGTH_LONG).show()
            }
        }
        val btnBefore = findViewById<ImageView>(R.id.btnBefore)
        btnBefore.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            imageName = "img_before_${dbConnect2.getDateAndTime()}.jpeg"
            imageBefore = imageName
            isCameraOne = true
            selectImage(this)
        }
        val btnAfter = findViewById<ImageView>(R.id.btnAfter)
        btnAfter.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            imageName = "img_after_${dbConnect2.getDateAndTime()}.jpeg"
            imageAfter = imageName
            isCameraOne = false
            selectImage(this)
        }
    }


    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(permission, PERMISSION_CODE_READ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(permissionCoarse, PERMISSION_CODE_WRITE) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                launchGallery()
            }
        }
    }


    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission was not enabled
                        val permission = arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE)
                    } else {
                        //permission already granted
                        openCamera()
                    }
                } else {
                    //system os is < marshmallow
                    openCamera()
                }
            } else if (options[item] == "Choose from Gallery") {
                checkPermissionForImage()
            }
        }
        builder.show()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun uploadImage(bitmap: Bitmap, imageName: String) {
        imageData ?: return
        val request = object : VolleyFileUploadRequest(
                Method.POST,
                postURL,
                Response.Listener {
                    println("response is: $it")
                },
                Response.ErrorListener {
                    println("error is: $it")
                }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["fileData"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }

            val plant = intent.getStringExtra("plant").toString()
            val date = dbConnect2.getDate()
            override fun getParams(): Map<String, String>? {
                val encodedString: String = Base64.encodeToString(
                        getFileDataFromDrawable(bitmap),
                        Base64.DEFAULT
                )
                val params: MutableMap<String, String> = HashMap()
                params["image"] = encodedString
                params["filename"] = imageName
                params["plant"] = plant.substring(8)
                params["data_yy"] = date.substring(0, 4)
                params["data_mm"] = date.substring(5, 7)
                params["data_dd"] = date.substring(8)
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }


    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    @Throws(IOException::class)
    private fun createImageData2(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData2 = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        imageView = findViewById(R.id.imageViewBefore)
        imageView2 = findViewById(R.id.imageViewAfter)
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode === RESULT_OK) {
                if (image_uri != null && isCameraOne) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(image_uri!!))
                    val matrix = Matrix()
                    matrix.postRotate(90F)
                    val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    bitmapTemp = bmp
                    imageView.setImageBitmap(bitmapTemp)
                    createImageData(image_uri!!)
                } else {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(image_uri!!))
                    val matrix = Matrix()
                    matrix.postRotate(90F)
                    val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    bitmapTemp2 = bmp
                    imageView2.setImageBitmap(bitmapTemp2)
                    createImageData2(image_uri!!)
                }
            } else if (resultCode === RESULT_CANCELED) {
                return
            }
            1 -> if (resultCode === RESULT_OK) {
                val uri = data?.data
                if (uri != null && isCameraOne) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
                    val matrix = Matrix()
                    matrix.postRotate(90F)
                    val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    bitmapTemp = bmp
                    imageView.setImageBitmap(bitmapTemp)
                    createImageData(uri!!)
                } else {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
                    val matrix = Matrix()
                    matrix.postRotate(90F)
                    val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    bitmapTemp2 = bmp
                    imageView2.setImageBitmap(bitmapTemp2)
                    createImageData2(uri!!)
                }
            } else if (resultCode === RESULT_CANCELED) {
                return
            }
        }
    }
}
