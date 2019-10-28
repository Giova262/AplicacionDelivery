package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class PendientesActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    var idUsuario: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pendientes)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("iduser",0)


        //.........................................................................

        enviarDatosAlServidor()

    }

    private fun enviarDatosAlServidor() {

        val url = config.URL.plus("/api/pedido/getPedidosUsuario/"+ idUsuario.toString() )

        val objetoJson= JSONObject()
        objetoJson.put("iduser", idUsuario )


        //...............Mando al servidor............................

       // val url = urlServidor.plus("/api/pedido/registrarPedido" )


        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: JsonObjectRequest( Request.Method.POST, url,objetoJson,

            Response.Listener<JSONObject> { response ->

                //pantalla_login()
                //mensaje_Toast(response.toString())

            },
            Response.ErrorListener {
                //mensaje_Toast("Ocurrio un Error!")
            })

        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Basic <<YOUR BASE64 USER:PASS>>"
                headers["Content-Type"] = "application/json; charset=UTF-8"
                headers["token"] = tokenUsario

                return headers
            }
        }

        queue.add(jsonObjectRequest)

    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }
}
