package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.login.LoginManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception


class LoginDeliveryActivity : AppCompatActivity() {

    private var tokenUsario :String = "-1"
    private var idUsuario: Int = 0
    private lateinit var datosUsuario : JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_delivery)

        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent
        datosUsuario = JSONObject( objetoIntent.getStringExtra("datos") )
        tokenUsario = objetoIntent.getStringExtra("token")

        //----------------------- Obtengo Elementos -----------------------

        val mensajeTextView = findViewById<TextView>(R.id.loginDelivery_mensajeTextView)
        val nombreTextView = findViewById<TextView>(R.id.loginDelivery_nombreTextView)
        val emailTextView = findViewById<TextView>(R.id.loginDelivery_emailTextView)
        val nivelTextView = findViewById<TextView>(R.id.loginDelivery_nivelTextView)
        val puntajeTextView = findViewById<TextView>(R.id.loginDelivery_puntajeTextView)

        val imagenTextView = findViewById<ImageView>(R.id.loginDelivery_ImageView)

        val tomarPedidoBoton = findViewById<Button>(R.id.loginDelivery_pedirButton)
        val miPedidoBoton = findViewById<Button>(R.id.loginDelivery_pendientesButton)
        val historialBoton = findViewById<Button>(R.id.loginDelivery_historialButton)
        val editarBoton = findViewById<Button>(R.id.loginDelivery_editarButton)
        val logoutBoton = findViewById<Button>(R.id.loginDelivery_logoutButton)

        //----------------------- Datos del Cliente -----------------------

        idUsuario = datosUsuario.getInt("id")

        mensajeTextView.setText("Seleccione Tomar Pedido para ver los pedidos en espera!")
        nombreTextView.setText( "Nombre: " + datosUsuario.getString("nombre"))
        emailTextView.setText( "Email: " + datosUsuario.getString("mail"))
        nivelTextView.setText( "Nivel: " + datosUsuario.getString("nivel"))
        puntajeTextView.setText( "Puntaje: " + datosUsuario.getString("puntaje"))

        //----------------------- FOTO -----------------------

        Picasso.get().load( datosUsuario.getString("foto") ).into(imagenTextView, object: com.squareup.picasso.Callback {

            override fun onError(e: Exception?) {
                imagenTextView.setImageDrawable(getResources().getDrawable(R.drawable.user))
            }

            override fun onSuccess() {

            }
        })

        //----------------------- Botones -----------------------

        tomarPedidoBoton?.setOnClickListener {

            consultarPedidosPendientes()
        }

        logoutBoton?.setOnClickListener {

            LoginManager.getInstance().logOut()
            pantalla_main()
            finish()
        }

        miPedidoBoton?.setOnClickListener {

            consultarMisPedidos()
        }

        historialBoton?.setOnClickListener {

            consultarHistorial()
        }

        editarBoton?.setOnClickListener {

            pantalla_editar()
        }

    }

    private fun consultarHistorial() {

        val url = config.URL.plus("/api/pedido/getHistorialDelivery/"+idUsuario.toString())

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)
                var pedidos= jsonob.getJSONArray("pedidos")

                pantalla_mi_historial( pedidos.toString() )

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

    private fun pantalla_mi_historial(pedidos: String) {
        val intent:Intent = Intent(this,HistorialDeliveryActivity::class.java)

        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("pedidiosPendientes",pedidos)
        intent.putExtra("datos",datosUsuario.toString())

        startActivity(intent)
    }

    private fun consultarMisPedidos() {

        val url = config.URL.plus("/api/pedido/getPedidosDelivery/"+idUsuario.toString())

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)
                var pedidos= jsonob.getJSONArray("pedidos")

                pantalla_mis_pedidos( pedidos.toString() )

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

    private fun pantalla_mis_pedidos(pedidos: String) {

        val intent:Intent = Intent(this,MisPedidosDeliveryActivity::class.java)

        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("pedidiosPendientes",pedidos)
        intent.putExtra("datos",datosUsuario.toString())

        startActivity(intent)
    }

    private fun pantalla_pedidos_pendientes( pedidos: String) {

        val intent:Intent = Intent(this,PedidoPendienteActivity::class.java)

        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("pedidiosPendientes",pedidos)
        intent.putExtra("datos",datosUsuario.toString())

        startActivity(intent)
    }

    private fun pantalla_editar() {

        val intent:Intent = Intent(this,EditarActivity::class.java)

        intent.putExtra("iduser",idUsuario)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("userData",datosUsuario.toString())

        startActivity(intent)
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun pantalla_main() {
        val intent:Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun consultarPedidosPendientes() {

        val url = config.URL.plus("/api/pedido/getPedidosPendientesParaDelivery/")

        //--------------------------ESTA HARDCODEADO-----------------------------------
                    // Buscar como obtener lat y long desde el telefono
        val jsonObject2 = JSONObject()
        jsonObject2.put("lati",-45.1)
        jsonObject2.put("longi",-50.1)
        //------------------------------------------------------------------------------

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: JsonObjectRequest( Request.Method.GET, url,jsonObject2,

            Response.Listener<JSONObject> { response ->

                var strResp = response.toString()

                val jsonob: JSONObject = JSONObject(strResp)
                var pedidos= jsonob.getJSONArray("pedidos")

                pantalla_pedidos_pendientes(pedidos.toString())
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
