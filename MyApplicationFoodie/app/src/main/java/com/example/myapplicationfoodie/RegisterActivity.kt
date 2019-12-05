package com.example.myapplicationfoodie

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.json.JSONObject
import java.io.IOException
import java.util.*

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    private var fotoUrl:String = "defecto"
    private var rolUsuario: Int = -1
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //----------------------- Obtengo Elementos -----------------------

        var registrarBoton = findViewById<Button>(R.id.register_registrarButton)
        var elegirfotoBoton = findViewById<Button>(R.id.register_elegirfotoButton)
        var imagen = findViewById<ImageView>(R.id.register_imageView)

        //----------------------- Foto por Defecto -----------------------

        imagen.setImageDrawable(getResources().getDrawable(R.drawable.user))


        //----------------------- Spinner -----------------------

        val spinner: Spinner = findViewById(R.id.register_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

        //----------------------- Firebase Store -----------------------

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()

        //.----------------------- Botones -----------------------

        elegirfotoBoton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                chooseImage()
            }
        })

        registrarBoton?.setOnClickListener {

            if( !fotoUrl.equals("defecto")){


                var passEditText = findViewById<EditText>( R.id.register_passEditText )
                var emailEditText = findViewById<EditText>( R.id.register_emailEditText )

                //--------------------- Creo en fire base el usuario tambien ---------------

                FirebaseAuth.getInstance().createUserWithEmailAndPassword( emailEditText.text.toString() , passEditText.text.toString() )
                    .addOnCompleteListener{
                        if( !it.isSuccessful ) return@addOnCompleteListener

                        uploadImage()

                        Log.d("Main","Exitoso al crear al usuario: ${it.result?.user?.uid}")
                    }
                    .addOnFailureListener {

                        mensaje_Toast("El mail debe tener un formato correcto @gmail.com por ejemplo.")
                        Log.d("Main","Error al crear al usuario: ${it.message}")
                    }



            }else{
                registerToServer()
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var imagen = findViewById<ImageView>(R.id.register_imageView)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data

            try {

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagen?.setImageBitmap(bitmap)

                fotoUrl = "No Default"  //Cambio la fotoUrl a cualquier string que no sea Default


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
                    fotoUrl = downloadUri.toString() // URL Real en Fire-Base
                    registerToServer()

                } else {

                    // Handle failures
                }
            }

        }else{

            mensaje_Toast("Error : No hay foto para Subir")
        }
    }

    private fun pantalla_main() {
        val intent:Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun registerToServer() {



        //----------------------- Obtengo Elementos -----------------------

        var nombreEditText = findViewById<EditText>( R.id.register_nombreEditText )
        var passEditText = findViewById<EditText>( R.id.register_passEditText )
        var emailEditText = findViewById<EditText>( R.id.register_emailEditText )

        
        //-----------------------  Creo el Json -----------------------

        val jsonObject1 = JSONObject()
        jsonObject1.put("nombre",nombreEditText.text)
        jsonObject1.put("pass",passEditText.text)
        jsonObject1.put("mail",emailEditText.text)
        jsonObject1.put("rol",rolUsuario)
        jsonObject1.put("puntaje",0)
        jsonObject1.put("nivel",0)
        jsonObject1.put("foto",fotoUrl)
        jsonObject1.put("cantEnvios",0)
        jsonObject1.put("redsocial","Ninguna")
        jsonObject1.put("uidfirebase","-1")


        //----------------------- Coneccion con Servidor -----------------------



            val queue = Volley.newRequestQueue( this )

            val url = config.URL.plus("/api/user/register")

            val jsonObjectRequest = JsonObjectRequest(url, jsonObject1,

                Response.Listener { response ->

                    var strResp = response.toString()
                    val jsonob: JSONObject = JSONObject(strResp)
                    val mensaje = jsonob.getString("message")

                    mensaje_Toast(mensaje)

                    pantalla_main()

                },
                Response.ErrorListener { error ->

                    error.printStackTrace()
                }
            )

            queue.add( jsonObjectRequest )






    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        rolUsuario = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
