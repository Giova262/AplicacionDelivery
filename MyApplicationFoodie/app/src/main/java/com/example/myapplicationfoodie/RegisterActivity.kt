package com.example.myapplicationfoodie

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.json.JSONObject
import java.io.IOException
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    private var fotoUrl:String = ""

    //Firebase
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //...........Obtengo Elementos...........................

        var mensajeTextView = findViewById<TextView>(R.id.register_mensajeTextView)

        var nombreEditText = findViewById<EditText>( R.id.register_nombreEditText )
        var passEditText = findViewById<EditText>( R.id.register_passEditText )
        var emailEditText = findViewById<EditText>( R.id.register_emailEditText )
       // var fotoEditText = findViewById<EditText>(R.id.register_fotoEditText)
        var clienteCheckBox = findViewById<CheckBox>(R.id.register_clienteCheckBox)
        var deliveryCheckBox = findViewById<CheckBox>(R.id.register_deliveryCheckBox)

        var registrarBoton = findViewById<Button>(R.id.register_registrarButton)
        var elegirfotoBoton = findViewById<Button>(R.id.register_elegirfotoButton)
        var subirfotoBoton = findViewById<Button>(R.id.register_subirfotoButton)

        //...................Firebase store.......................

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()

        //........................BOTONES................................

        subirfotoBoton.setEnabled(false)

        elegirfotoBoton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                chooseImage()
            }
        })

        subirfotoBoton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                uploadImage()
            }
        })

        //................Enviar datos..............................

        registrarBoton?.setOnClickListener {


            if( fotoUrl != ""){

                if(nombreEditText.text.isNotBlank() && passEditText.text.isNotBlank() &&
                    emailEditText.text.isNotBlank() ){


                    //............Chekeo que elija un rol..............................

                    if ( clienteCheckBox.isChecked && !deliveryCheckBox.isChecked ){


                        //..............Genero Json Para enviar al servidor.................

                        var nombre = nombreEditText.text
                        var pass = passEditText.text
                        var mail = emailEditText.text
                        var rol = 0
                        var puntaje = 0
                        var nivel = 0
                        var foto = fotoUrl
                        var cantEnvios = 0
                        var redsocial = "ninguna"
                        var uidfirebase = "-1"

                        val jsonObject = JSONObject()
                        jsonObject.put("nombre",nombre)
                        jsonObject.put("pass",pass)
                        jsonObject.put("mail",mail)
                        jsonObject.put("rol",rol)
                        jsonObject.put("puntaje",puntaje)
                        jsonObject.put("nivel",nivel)
                        jsonObject.put("foto",foto)
                        jsonObject.put("cantEnvios",cantEnvios)
                        jsonObject.put("redsocial",redsocial)
                        jsonObject.put("uidfirebase",uidfirebase)

                        //...................................................................

                        enviarDatosAlServidor(jsonObject)

                    }

                    else if ( !clienteCheckBox.isChecked && deliveryCheckBox.isChecked ){

                        //..............Genero Json Para enviar al servidor.................

                        var nombre = nombreEditText.text
                        var pass = passEditText.text
                        var mail = emailEditText.text
                        var rol = 1
                        var puntaje = 0
                        var nivel = 0
                        var foto = fotoUrl
                        var cantEnvios = 0
                        var redsocial = "ninguna"
                        var uidfirebase = "-1"

                        val jsonObject = JSONObject()
                        jsonObject.put("nombre",nombre)
                        jsonObject.put("pass",pass)
                        jsonObject.put("mail",mail)
                        jsonObject.put("rol",rol)
                        jsonObject.put("puntaje",puntaje)
                        jsonObject.put("nivel",nivel)
                        jsonObject.put("foto",foto)
                        jsonObject.put("cantEnvios",cantEnvios)
                        jsonObject.put("redsocial",redsocial)
                        jsonObject.put("uidfirebase",uidfirebase)

                        //...................................................................

                        enviarDatosAlServidor(jsonObject)

                    }
                    else {
                        mensajeTextView.setText("Debes Elegir un Rol")
                    }
                    //.........................................................

                }else{

                    mensajeTextView.setText("Campos sin completar. Rellena todos los campos Por Favor!")
                }

            }else{
                mensaje_Toast("Debes subir la foto antes , presiona el boton Subir")
            }

        }

        //.........................................................

    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       // var fotoEditText = findViewById<EditText>(R.id.register_fotoEditText)
        var subirfotoBoton = findViewById<Button>(R.id.register_subirfotoButton)
        var imagen = findViewById<ImageView>(R.id.register_imageView)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data

            try {

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagen?.setImageBitmap(bitmap)
                subirfotoBoton.setEnabled(true)


            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun uploadImage() {

       // var fotoEditText = findViewById<EditText>(R.id.register_fotoEditText)

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
                    fotoUrl = downloadUri.toString()
                   // println("url:"  + downloadUri ) //aca tengo la URL

                   // fotoEditText.setText( downloadUri.toString() )


                } else {
                    // Handle failures
                }
            }

        }else{

            mensaje_Toast("Error : No hay foto para subir")
        }
    }

    private fun pantalla_loginDelivery() {
        /*val intent:Intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)*/
    }

    private fun pantalla_loginUsuario() {
        val intent:Intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun pantalla_main() {
        val intent:Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun enviarDatosAlServidor(jsonObject: JSONObject) {

        val queue = Volley.newRequestQueue( this )
        //val url = "https://polar-stream-82449.herokuapp.com/api/user/register"
        val url = config.URL.plus("/api/user/register")


        val jsonObjectRequest = JsonObjectRequest(url, jsonObject,

            Response.Listener { response ->

                //................ Respuesta Json del Servidor...................

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)
                val mensaje = jsonob.getString("message")
                val token = jsonob.getString("token")
                mensaje_Toast(mensaje + token )

                pantalla_main()

                //................................................................
            },
            Response.ErrorListener { error ->

                error.printStackTrace()
            }
        )

        queue.add( jsonObjectRequest )

    }
}
