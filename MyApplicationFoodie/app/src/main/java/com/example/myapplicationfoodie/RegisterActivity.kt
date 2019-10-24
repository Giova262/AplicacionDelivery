package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    //val urlServidor = "https://polar-stream-82449.herokuapp.com"
    val urlServidor = "http://192.168.0.4:5000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //...........Obtengo Elementos...........................

        var mensajeTextView = findViewById<TextView>(R.id.register_mensajeTextView)

        var nombreEditText = findViewById<EditText>( R.id.register_nombreEditText )
        var passEditText = findViewById<EditText>( R.id.register_passEditText )
        var emailEditText = findViewById<EditText>( R.id.register_emailEditText )
        var fotoEditText = findViewById<EditText>(R.id.register_fotoEditText)
        var clienteCheckBox = findViewById<CheckBox>(R.id.register_clienteCheckBox)
        var deliveryCheckBox = findViewById<CheckBox>(R.id.register_deliveryCheckBox)

        var registrarBoton = findViewById<Button>(R.id.register_registrarButton)

        //........................................................


        //................Enviar datos..............................

        registrarBoton?.setOnClickListener {

            if(nombreEditText.text.isNotBlank() && passEditText.text.isNotBlank() &&
                emailEditText.text.isNotBlank() && fotoEditText.text.isNotBlank()){


                //............Chekeo que elija un rol..............................

                if ( clienteCheckBox.isChecked && !deliveryCheckBox.isChecked ){


                    //..............Genero Json Para enviar al servidor.................

                    var nombre = nombreEditText.text
                    var pass = passEditText.text
                    var mail = emailEditText.text
                    var rol = 0
                    var puntaje = 0
                    var nivel = 0
                    var foto = fotoEditText.text
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

                    //enviarDatosAlServidor()
                    //pantalla_loginDelivery()

                }
                else {
                    mensajeTextView.setText("Debes Elegir un Rol")
                }
                //.........................................................

            }else{

                mensajeTextView.setText("Campos sin completar. Rellena todos los campos Por Favor!")
            }

        }

        //.........................................................

    }

    private fun pantalla_loginDelivery() {
        val intent:Intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun pantalla_loginUsuario() {
        val intent:Intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun enviarDatosAlServidor(jsonObject: JSONObject) {

        val queue = Volley.newRequestQueue( this )
        //val url = "https://polar-stream-82449.herokuapp.com/api/user/register"
        val url = urlServidor.plus("/api/user/register")


        val jsonObjectRequest = JsonObjectRequest(url, jsonObject,

            Response.Listener { response ->

                //................ Respuesta Json del Servidor...................

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)
                val mensaje = jsonob.getString("message")
                val token = jsonob.getString("token")
                mensaje_Toast(mensaje + token )

                //pantalla_loginUsuario()
                //................................................................
            },
            Response.ErrorListener { error ->

                error.printStackTrace()
            }
        )

        queue.add( jsonObjectRequest )

    }
}
