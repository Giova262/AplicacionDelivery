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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class EditarActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var tokenUsario :String = "-1"
    private var idUsuario: Int = 0
    private var rolUsuario: Int = -1
    private lateinit var datosUsuario:String
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    private var fotoUrl:String = ""
    private var fotoUrlTemp:String = ""
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar)

        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("iduser",0)
        datosUsuario = objetoIntent.getStringExtra("userData")

        val usuario: JSONObject = JSONObject(datosUsuario)

        //----------------------- Obtengo Elementos -----------------------

        val nombreEditText = findViewById<EditText>( R.id.editar_nombreEditText )
        val passEditText = findViewById<EditText>( R.id.editar_passEditText )
        val emailEditText = findViewById<EditText>( R.id.editar_mailEditText )
        val redsocialEditText = findViewById<EditText>(R.id.editar_redsocialEditText)
        val cambiarBoton = findViewById<Button>(R.id.editar_cambiarButton)
        val volverBoton = findViewById<Button>(R.id.editar_volverButton)
        val elegirfotoBoton = findViewById<Button>(R.id.editar_elegirbutton)
        val imagen = findViewById<ImageView>(R.id.editar_imageView)

        //----------------------- Muestro Datos -----------------------

        nombreEditText.setText( usuario.getString("nombre")  )
        passEditText.setText( usuario.getString("pass")  )
        emailEditText.setText( usuario.getString("mail")  )
        redsocialEditText.setText( usuario.getString("redsocial")  )

        //----------------------- Foto de Usuario  -----------------------

        fotoUrl = usuario.getString("foto")
        fotoUrlTemp = usuario.getString("foto")

        Picasso.get().load( usuario.getString("foto") ).into(imagen, object: com.squareup.picasso.Callback {

            override fun onError(e: Exception?) {

                imagen.setImageDrawable(getResources().getDrawable(R.drawable.user))
            }

            override fun onSuccess() {

            }
        })

        //----------------------- Fire-base Store -----------------------

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()

        //----------------------- Botones -----------------------

        elegirfotoBoton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                chooseImage()
            }
        })

        cambiarBoton?.setOnClickListener {

            if( !fotoUrl.equals( fotoUrlTemp )){

                uploadImage()

            }else{

                enviarDatosAlServidor()
            }
        }

        volverBoton?.setOnClickListener {

            pantalla_login()
        }

        //----------------------- Spinner -----------------------

        val spinner: Spinner = findViewById(R.id.editar_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var imagen = findViewById<ImageView>(R.id.editar_imageView)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data

            try {

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagen?.setImageBitmap(bitmap)

                fotoUrl = "Nueva"  // Pongo cualquier cosa con tal que sea distinto al original


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

                    enviarDatosAlServidor()

                } else {
                    // Handle failures
                }
            }

        }else{

            mensaje_Toast("Error : No hay foto para subir")
        }
    }

    private fun pantalla_login() {

        val intent:Intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("datos",datosUsuario)

        startActivity(intent)
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun enviarDatosAlServidor() {

        val nombreEditText = findViewById<EditText>( R.id.editar_nombreEditText )
        val passEditText = findViewById<EditText>( R.id.editar_passEditText )
        val emailEditText = findViewById<EditText>( R.id.editar_mailEditText )
        val redsocialEditText = findViewById<EditText>(R.id.editar_redsocialEditText)

        val jsonObject = JSONObject()
        jsonObject.put("id",idUsuario)
        jsonObject.put("nombre",nombreEditText.text)
        jsonObject.put("mail",emailEditText.text)
        jsonObject.put("pass",passEditText.text)
        jsonObject.put("rol",rolUsuario)
        jsonObject.put("foto",fotoUrl )
        jsonObject.put("redsocial",redsocialEditText.text)

        val url = config.URL.plus("/api/user/"+idUsuario.toString() )

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: JsonObjectRequest( Request.Method.PUT, url,jsonObject,

            Response.Listener<JSONObject> { response ->

                getDatosUsuarioActualizados()

            },
            Response.ErrorListener {  })

        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Basic <<YOUR BASE64 USER:PASS>>"
                headers["Content-Type"] = "application/json; charset=UTF-8"
                headers["token"] = tokenUsario;

                return headers
            }
        }

        queue.add(jsonObjectRequest)

    }

    private fun getDatosUsuarioActualizados() {

        val url = config.URL.plus("/api/user/"+ idUsuario.toString() )

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)

                datosUsuario =  jsonob.toString()

                mensaje_Toast("Cambios Realizados Correctamente! ")
                pantalla_login()

            },
            Response.ErrorListener {  })

        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Basic <<YOUR BASE64 USER:PASS>>"
                headers["Content-Type"] = "application/json; charset=UTF-8"
                headers["token"] = tokenUsario;

                return headers
            }
        }

        queue.add(jsonObjectRequest)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        rolUsuario = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}


