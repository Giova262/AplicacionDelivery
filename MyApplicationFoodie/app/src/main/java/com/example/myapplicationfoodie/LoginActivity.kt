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
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    val urlServidor = "https://polar-stream-82449.herokuapp.com"
    //val urlServidor = "http://192.168.0.4:5000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent=intent
        val datos: JSONObject = JSONObject( objetoIntent.getStringExtra("datos") )
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

        mensajeTextView.setText("Bienvenido! Elija Pedir para encontrar lo que buscas!")
        nombreTextView.setText( "Nombre: " + datos.getString("nombre"))
        emailTextView.setText( "Email: " + datos.getString("mail"))
        nivelTextView.setText( "Nivel: " + datos.getString("nivel"))
        puntajeTextView.setText( "Puntaje: " + datos.getString("puntaje"))

        imagenTextView.setImageDrawable(getResources().getDrawable(R.drawable.user));

        //.........................................................................

        //....................Manejo de botones ..................................

        pedirBoton?.setOnClickListener {

            enviarDatosAlServidor(null)
        }

        logoutBoton?.setOnClickListener {
            LoginManager.getInstance().logOut()
            pantalla_main()
            finish()
        }

    }


    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun pantalla_main() {
        val intent:Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun pantalla_pedir( datos :String ) {
        val intent:Intent = Intent(this,ComercioActivity::class.java)
        intent.putExtra("datos",datos)
        intent.putExtra("token",tokenUsario)
        startActivity(intent)
        finish()
    }

    private fun enviarDatosAlServidor(jsonObject: JSONObject?) {


        val url = urlServidor.plus("/api/comercio/all")


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
