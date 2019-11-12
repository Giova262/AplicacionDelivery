package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PedidoOpcionesClienteActivity : AppCompatActivity() {

    private var tokenUsario :String = "-1"
    private var datosUsuario :String = "-1"
    private var idUsuario: Int = 0
    private var pedido: String = ""
    private var latitud:Double = 0.0
    private var longitud:Double = 0.0
    private var pedidoId:Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_opciones_cliente)

        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("idUsuario",0)
        pedido = objetoIntent.getStringExtra("pedido")
        datosUsuario = objetoIntent.getStringExtra("datos")

        //----------------------- Obtengo Lat/Long del Pedido -----------------------

        var pedidoObj = JSONObject(pedido)
        latitud = pedidoObj.getDouble("ped_latitudinicio")
        longitud = pedidoObj.getDouble("ped_longitudinicio")
        pedidoId = pedidoObj.getInt("ped_id")


        //----------------------- Obtengo Elementos -----------------------

        var mapaBoton = findViewById<Button>(R.id.pedOpcCliente_mapabutton)
        var chatBoton = findViewById<Button>(R.id.pedOpcCliente_chatbutton)
        var entregadoBoton = findViewById<Button>(R.id.pedOpcCliente_entregadobutton)


        //----------------------- Botones -----------------------

        mapaBoton?.setOnClickListener {
            pantalla_mapa()
        }

        entregadoBoton?.setOnClickListener {

            var estado = pedidoObj.getInt("ped_estado")
            if( estado == 2 ){
                confirmarRecibiPedido()
            }else mensaje_Toast("No puedes confirmar la entrega de un pedido que no fue Tomado aun.")

        }

        chatBoton?.setOnClickListener{

            var estado = pedidoObj.getInt("ped_estado")
            if( estado == 2 ){
                //confirmarRecibiPedido()
            }else mensaje_Toast("Tu pedido esta Pendiente, No hay delivery para iniciar el Chat")
        }


    }

    private fun confirmarRecibiPedido() {

        val jsonObject = JSONObject()
        jsonObject.put("ped_id",pedidoId)
        jsonObject.put("ped_userid",idUsuario)
        jsonObject.put("estado",3)


        val url = config.URL.plus("/api/pedido/" )

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: JsonObjectRequest( Request.Method.PUT, url,jsonObject,

            Response.Listener<JSONObject> { response ->

               // mensaje_Toast(response.toString())
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

    private fun pantalla_login() {

        val intent:Intent = Intent(this,LoginActivity::class.java)

        intent.putExtra("token",tokenUsario)
        intent.putExtra("datos",datosUsuario)

        startActivity(intent)
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun pantalla_mapa( ) {
        val intent:Intent = Intent(this,MapsActivity::class.java)

        intent.putExtra("lat",latitud)
        intent.putExtra("long",longitud)

        startActivity(intent)

    }
}
