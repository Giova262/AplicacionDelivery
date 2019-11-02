package com.example.myapplicationfoodie

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class EditarActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var tokenUsario :String = "-1"
    var idUsuario: Int = 0
    var rolUsuario: Int = -1
    lateinit var datosUsuario:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar)

        //...........Obtengo Elementos...........................

        var mensajeTextView = findViewById<TextView>(R.id.editar_mensajeTextView)

        var nombreEditText = findViewById<EditText>( R.id.editar_nombreEditText )
        var passEditText = findViewById<EditText>( R.id.editar_passEditText )
        var emailEditText = findViewById<EditText>( R.id.editar_mailEditText )
        var fotoEditText = findViewById<EditText>(R.id.editar_fotoEditText)
        var redsocialEditText = findViewById<EditText>(R.id.editar_redsocialEditText)

        var cambiarBoton = findViewById<Button>(R.id.editar_cambiarButton)
        var volverBoton = findViewById<Button>(R.id.editar_volverButton)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("iduser",0)
        datosUsuario = objetoIntent.getStringExtra("userData")

        val usuario: JSONObject = JSONObject(datosUsuario)

        //.......................................................................

        nombreEditText.setText( usuario.getString("nombre")  )
        passEditText.setText( usuario.getString("pass")  )
        emailEditText.setText( usuario.getString("mail")  )
        fotoEditText.setText( usuario.getString("foto")  )
        redsocialEditText.setText( usuario.getString("redsocial")  )

        //...............................SPINER.....................................

        val spinner: Spinner = findViewById(R.id.editar_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

        //...........................................................................

        cambiarBoton?.setOnClickListener {

            val jsonObject = JSONObject()
            jsonObject.put("id",idUsuario)
            jsonObject.put("nombre",nombreEditText.text)
            jsonObject.put("mail",emailEditText.text)
            jsonObject.put("pass",passEditText.text)
            jsonObject.put("rol",rolUsuario)
            jsonObject.put("foto",fotoEditText.text)
            jsonObject.put("redsocial",redsocialEditText.text)

            enviarDatosAlServidor( jsonObject )

        }

        volverBoton?.setOnClickListener {

            pantalla_login()
        }


    }

    private fun pantalla_login() {

        mensaje_Toast("Cambios realizados Exitosamente! ")
        val intent:Intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("datos",datosUsuario)

        startActivity(intent)
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun enviarDatosAlServidor( jsonObject :JSONObject) {

        val url = config.URL.plus("/api/user/1" )


        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: JsonObjectRequest( Request.Method.PUT, url,jsonObject,

            Response.Listener<JSONObject> { response ->

                var strResp = response.toString()
                //val jsonob: JSONObject = JSONObject(strResp)

                mensaje_Toast( strResp )

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


