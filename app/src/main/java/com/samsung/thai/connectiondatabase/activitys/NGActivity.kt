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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.samsung.thai.connectiondatabase.*
import com.samsung.thai.connectiondatabase.dbHelper.dbConnect2
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.log

class NGActivity : AppCompatActivity() {
    var bitmapTemp: Bitmap? = null
    var bitmapTemp2: Bitmap? = null
    lateinit var imageView: ImageView
    lateinit var imageView2: ImageView
    companion object{
        var dbConnect2 : dbConnect2 = dbConnect2()
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
        var imageName = ""
        var imageBefore =""
        var imageAfter = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        if(resources.getBoolean(R.bool.portrait_only)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        val plant = intent.getStringExtra("plant").toString()
        val txtDayName = findViewById<TextView>(R.id.txtDayName)
        val txtDate = findViewById<TextView>(R.id.txtDate)
        val txtContentName = findViewById<TextView>(R.id.txtDetail)
        val week = intent.getStringExtra("week").toString()
        val contentCheck = intent.getStringExtra("contentName").toString()
        var date = dbConnect2.getDate()
        val checkID = intent.getStringExtra("check_id").toString()
        val machineID = intent.getStringExtra("machine_id").toString()
        val contentID = intent.getStringExtra("content_id").toString()
        val imageUri = dbConnect2.getImageUrl(checkID,machineID,contentID,week)

        if(imageUri != null){
            val imageView = findViewById<ImageView>(R.id.imageViewBefore)
            Picasso.get().load(imageUri).into(imageView)
        }

        if(date.isNotEmpty()){
            date = date.substring(8) + "/" + date.substring(5, 7) + "/" + date.substring(0, 4)
        }
        txtDayName.text = "Week : $week"
        txtDate.text = "Date : $date"
        txtContentName.text = "Content Check : $contentCheck"
        val status : ArrayList<String> = ArrayList(listOf(*resources.getStringArray(R.array.CheckStatus)))
        val spinner = findViewById<Spinner>(R.id.spinner_status)
        if (spinner != null) {
//            val adapter = ArrayAdapter(this, R.layout.spinner_item, checkStatus)

            val customDropDownAdapter = CustomDropDownAdapter(this, status)
            spinner.adapter = customDropDownAdapter
            spinner.setSelection(0)
        }

        val btnBefore = findViewById<ImageView>(R.id.btnBefore)
        btnBefore.setImageResource(R.drawable.ic_camera2)

        val btnAfter = findViewById<ImageView>(R.id.btnAfter)
        btnAfter.setOnClickListener {
                it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
                imageName = "img_after_${dbConnect2.getDateAndTime()}.jpeg"
                imageAfter = imageName
                isCameraOne = false
                selectImage(this)
        }

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etComment = findViewById<EditText>(R.id.etComment)

        btnSubmit.setOnClickListener {
            val loadingDialog = LoadingDialog(this@NGActivity)
            val checkStatus = spinner.selectedItem.toString()
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 1500) }
            if(bitmapTemp2 != null) {
                if(checkStatus == "Finish"){
                    var date = dbConnect2.getDate()
                    Log.d("TestDate",date)
                    val empID = intent.getStringExtra("empID").toString()
                    uploadImage(bitmapTemp2!!, imageAfter)
                    var imageLink2 = "http://107.101.81.9:11111/machine_audit/img_mobile/" +
                            "${plant}/" +
                            "${date.substring(0, 4)}/" +
                            "${date.substring(5, 7)}/" +
                            "${date.substring(8)}/" +
                            "$imageAfter"
                    dbConnect2.updateNG(imageLink2, empID, date, etComment.text.toString(), checkID, machineID, contentID, week)
                    loadingDialog.startLoadingDialog()
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        finish()
                        loadingDialog.dismissDialog()
                    }, 1000)
                }else{
                    Toast.makeText(this,"Please change status to Finish",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Please fill all fields",Toast.LENGTH_LONG).show()
            }
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
        val plant = intent.getStringExtra("plant").toString()
        var dateChange: String = dbConnect2.getDate()
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



            override fun getParams(): Map<String, String>? {
                val encodedString: String = Base64.encodeToString(
                        getFileDataFromDrawable(bitmap),
                        Base64.DEFAULT
                )
                val params: MutableMap<String, String> = HashMap()
                params["image"] = encodedString
                params["filename"] = imageName
                params["plant"] = plant
                params["data_yy"] = dateChange.substring(0,4)
                params["data_mm"] = dateChange.substring(5,7)
                params["data_dd"] = dateChange.substring(8)
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
                    createImageData(image_uri!!)
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
                    createImageData(uri!!)
                }
            } else if (resultCode === RESULT_CANCELED) {
                return
            }
        }
    }
}

