package com.example.myapplicationfoodie

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.content.Intent
import android.provider.MediaStore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

import android.app.ProgressDialog
import android.widget.Toast
import java.util.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.BufferedInputStream
import java.net.URL
import android.os.AsyncTask.execute
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso


class PruebasActivity : AppCompatActivity() {

    private var btnChoose: Button? = null
    private var btnUpload: Button? = null
    private var imageView: ImageView? = null

    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST = 71


    //Firebase
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pruebas)

        ///


        //Initialize Views
        btnChoose =  findViewById<Button>(R.id.btnChoose)
        btnUpload = findViewById<Button>(R.id.btnUpload)
        imageView =  findViewById<ImageView>(R.id.imgView)

        //
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()

        //

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/my-application-foodie.appspot.com/o/images%2Fd419382a-27b6-452b-92b1-24a6bdc1c80a?alt=media&token=c7455f7d-cabe-4989-9917-419a1069322a").into(imageView);


       btnChoose?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                chooseImage()
            }
        })

        btnUpload?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                uploadImage()
            }
        })

    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView?.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    private fun uploadImage() {

        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref = storageReference?.child("images/" + UUID.randomUUID().toString())

            var uploadTask = ref?.putFile(filePath!!)

                ?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")

                    if( progress == 100.0){
                        progressDialog.dismiss()
                    }
                }
                ?.addOnSuccessListener {

                    mensaje_Toast( "Foto subida satisfactoriamente." )

                }

            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref?.downloadUrl


            })?.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val downloadUri = task.result

                    println("url:"  + downloadUri ) //aca tengo la URL
                    mensaje_Toast(downloadUri.toString())

                } else {
                    // Handle failures
                }
            }

        }
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

}
