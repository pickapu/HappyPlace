package com.example.happyplaces

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding:ActivityAddHappyPlaceBinding
    private var cal=Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.tbAddplace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAddplace.setNavigationOnClickListener {
            onBackPressed()
        }
        dateSetListener=DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            cal.set(Calendar.YEAR,i)
            cal.set(Calendar.MONTH,i2)
            cal.set(Calendar.DAY_OF_MONTH,i3)
            updateDateInView()
        }
        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date->{
                DatePickerDialog(this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

            }
            R.id.tv_add_image->{
                val pictureDialog=AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogITem= arrayOf("Select photo from Gallery","Capture photo from camera")
                pictureDialog.setItems(pictureDialogITem){
                    dialog,which->
                    when(which){
                        0->{
                            choosePhotoFromGallery()
                        }
                        1->{
                            takePhotoFromCamera()
                        }
                    }
                }
                pictureDialog.show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== GALLERY){
                if(data!=null){
                   val  contentURI=data.data
                    try{
                        var selectedImageBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                    saveImageToINeternalStorage(selectedImageBitmap)
                        binding.ivPlaceImage.setImageBitmap(selectedImageBitmap)
                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                }
            }else if(requestCode==CAMERA){
                val thumbnail:Bitmap=data!!.extras!!.get("data") as Bitmap
                saveImageToINeternalStorage(thumbnail)
                binding.ivPlaceImage.setImageBitmap(thumbnail)
            }
        }
    }
    private fun takePhotoFromCamera(){
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if (p0 != null) {
                    if(p0.areAllPermissionsGranted()){
                        val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(cameraIntent,CAMERA)
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationalDialogForPermission()
            }




        }).onSameThread().check()
    }
    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if (p0 != null) {
                    if(p0.areAllPermissionsGranted()){
                       val galleryIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent,GALLERY)
                    }  
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationalDialogForPermission()
            }




        }).onSameThread().check()
    }
    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(this).setMessage("permission denied can be enabled under app setting").setPositiveButton("Go to Setting"){
            _,_->
            try{
                val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri= Uri.fromParts("package",packageName,null)
                intent.data=uri
                startActivity(intent)
            }catch (e:ActivityNotFoundException){
                e.printStackTrace()
            }
        }.setNegativeButton("cancel"){
            dialog,_->dialog.dismiss()
        }.show()
    }
    private fun updateDateInView(){
        val myFormat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myFormat,Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time).toString())
    }
    private  fun saveImageToINeternalStorage(bitmap:Bitmap):Uri{
     val wrapper=ContextWrapper(applicationContext)
     var file=wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
     file= File(file,"${UUID.randomUUID()}.jpg")
     try{
         val stream:OutputStream=FileOutputStream(file)
         bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
         stream.flush()
         stream.close()
     }catch (e:IOException){
         e.printStackTrace()
     }
        return Uri.parse(file.absolutePath)
    }
    companion object{
        private const val GALLERY=1
        private const val CAMERA=2
        private  const val IMAGE_DIRECTORY="happyPLaceImage"
    }
}