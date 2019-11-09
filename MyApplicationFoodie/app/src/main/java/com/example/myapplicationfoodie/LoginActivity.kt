package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.login.LoginManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    var idUsuario: Int = 0
    lateinit var datosUsuario :JSONObject


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent=intent
        datosUsuario = JSONObject( objetoIntent.getStringExtra("datos") )
        tokenUsario = objetoIntent.getStringExtra("token")


        //.......................................................................

        //...........Obtengo Elementos.............................................

        var mensajeTextView = findViewById<TextView>(R.id.loginCliente_mensajeTextView)
        var nombreTextView = findViewById<TextView>(R.id.loginCliente_nombreTextView)
        var emailTextView = findViewById<TextView>(R.id.loginCliente_emailTextView)
        var nivelTextView = findViewById<TextView>(R.id.loginCliente_nivelTextView)
        var puntajeTextView = findViewById<TextView>(R.id.loginCliente_puntajeTextView)

        var imagenTextView = findViewById<ImageView>(R.id.loginCliente_ImageView)

        var pedirBoton = findViewById<Button>(R.id.loginCliente_pedirButton)
        var pendienteBoton = findViewById<Button>(R.id.loginCliente_pendientesButton)
        var historialBoton = findViewById<Button>(R.id.loginCliente_historialButton)
        var editarBoton = findViewById<Button>(R.id.loginCliente_editarButton)
        var logoutBoton = findViewById<Button>(R.id.loginCliente_logoutButton)


        //........................................................................

        //................Muestro datos del cliente...............................

        idUsuario = datosUsuario.getInt("id")


        mensajeTextView.setText("Bienvenido! Elija Pedir para encontrar lo que buscas!")
        nombreTextView.setText( "Nombre: " + datosUsuario.getString("nombre"))
        emailTextView.setText( "Email: " + datosUsuario.getString("mail"))
        nivelTextView.setText( "Nivel: " + datosUsuario.getString("nivel"))
        puntajeTextView.setText( "Puntaje: " + datosUsuario.getString("puntaje"))


        //...........................FOTO..............................................


        Picasso.get().load( datosUsuario.getString("foto") ).into(imagenTextView, object: com.squareup.picasso.Callback {

                override fun onError(e: Exception?) {

                    mensaje_Toast("Imagen por defecto")
                    imagenTextView.setImageDrawable(getResources().getDrawable(R.drawable.user))
                }

                override fun onSuccess() {

                }
        })


        //....................Manejo de botones ..................................

        pedirBoton?.setOnClickListener {

            enviarDatosAlServidor(null)
        }

        logoutBoton?.setOnClickListener {
            LoginManager.getInstance().logOut()
            pantalla_main()
            finish()
        }

        pendienteBoton?.setOnClickListener {

            pantalla_pendientes()
        }

        historialBoton?.setOnClickListener {

            pantalla_historial()
        }

        editarBoton?.setOnClickListener {

            pantalla_editar()
        }

    }

    private fun pantalla_editar() {

        val intent:Intent = Intent(this,EditarActivity::class.java)

        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("userData",datosUsuario.toString())
        startActivity(intent)
    }

    private fun pantalla_historial() {
        val intent:Intent = Intent(this,HistorialActivity::class.java)

        //intent.putExtra("userData",datosUsuario.toString())
        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        startActivity(intent)
    }

    private fun pantalla_pendientes() {
        val intent:Intent = Intent(this,PendientesActivity::class.java)

        //intent.putExtra("userData",datosUsuario.toString())
        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        startActivity(intent)
        //finish()
    }


    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun pantalla_main() {
        val intent:Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun pantalla_pedir( comercios :String ) {
        val intent:Intent = Intent(this,ComercioActivity::class.java)
        intent.putExtra("datos",comercios)
        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("userData",datosUsuario.toString())

        startActivity(intent)
        finish()
    }

    private fun enviarDatosAlServidor(jsonObject: JSONObject?) {


        val url = config.URL.plus("/api/comercio/all")


        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)
                var comercios= jsonob.getJSONArray("data")

                pantalla_pedir(comercios.toString())


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

}
