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
import org.json.JSONObject

class LoginDeliveryActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    var idUsuario: Int = 0
    lateinit var datosUsuario : JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_delivery)

        //.....................Recibo datos ................................................

        val objetoIntent : Intent =intent
        datosUsuario = JSONObject( objetoIntent.getStringExtra("datos") )
        tokenUsario = objetoIntent.getStringExtra("token")

        //.....................Obtengo Elementos.............................................

        var mensajeTextView = findViewById<TextView>(R.id.loginDelivery_mensajeTextView)
        var nombreTextView = findViewById<TextView>(R.id.loginDelivery_nombreTextView)
        var emailTextView = findViewById<TextView>(R.id.loginDelivery_emailTextView)
        var nivelTextView = findViewById<TextView>(R.id.loginDelivery_nivelTextView)
        var puntajeTextView = findViewById<TextView>(R.id.loginDelivery_puntajeTextView)

        var imagenTextView = findViewById<ImageView>(R.id.loginDelivery_ImageView)

        var tomarPedidoBoton = findViewById<Button>(R.id.loginDelivery_pedirButton)
        var miPedidoBoton = findViewById<Button>(R.id.loginDelivery_pendientesButton)
        var historialBoton = findViewById<Button>(R.id.loginDelivery_historialButton)
        var editarBoton = findViewById<Button>(R.id.loginDelivery_editarButton)
        var logoutBoton = findViewById<Button>(R.id.loginDelivery_logoutButton)

        //................Muestro datos del cliente...............................

        idUsuario = datosUsuario.getInt("id")


        mensajeTextView.setText("Seleccione Tomar Pedido para ver los pedidos en espera!")
        nombreTextView.setText( "Nombre: " + datosUsuario.getString("nombre"))
        emailTextView.setText( "Email: " + datosUsuario.getString("mail"))
        nivelTextView.setText( "Nivel: " + datosUsuario.getString("nivel"))
        puntajeTextView.setText( "Puntaje: " + datosUsuario.getString("puntaje"))

        imagenTextView.setImageDrawable(getResources().getDrawable(R.drawable.user))

        //....................Manejo de botones ..................................

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

                mensaje_Toast(pedidos.toString())

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

                mensaje_Toast(pedidos.toString())

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

       // intent.putExtra("userData",datosUsuario.toString())
        startActivity(intent)
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

    private fun consultarPedidosPendientes() {


        val url = config.URL.plus("/api/pedido/getPedidosPendientesParaDelivery/")

        val jsonObject2 = JSONObject()
        jsonObject2.put("lati",-45.1)
        jsonObject2.put("longi",-50.1)



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
