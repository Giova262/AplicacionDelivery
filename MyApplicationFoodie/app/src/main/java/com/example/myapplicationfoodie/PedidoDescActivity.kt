package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PedidoDescActivity : AppCompatActivity() {

    private var tokenUsario :String = "-1"
    private var idUsuario: Int = 0
    private lateinit var pedido: JSONObject
    private var pedidos :String = "-1"
    private var datosUsuario :String = "-1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_desc)

        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        pedido = JSONObject( objetoIntent.getStringExtra("pedido") )
        pedidos = objetoIntent.getStringExtra("pedidos")
        datosUsuario = objetoIntent.getStringExtra("datos")

        val usuarioDatos =JSONObject(datosUsuario)
        idUsuario = usuarioDatos.getInt("id")

        //----------------------- Obtengo Elementos -----------------------

        var idUsuarioTextView = findViewById<TextView>(R.id.pedidoDesc_userid_textView)
        var precioTotalTextView = findViewById<TextView>(R.id.pedidoDesc_totalprecio_textView)
        var dirOrigenTextView = findViewById<TextView>(R.id.pedidoDesc_dirOrigen_textView)
        var dirDestinoEditText = findViewById<TextView>(R.id.pedidoDesc_dirDestino_textView)

        var tomarButton = findViewById<Button>(R.id.pedidoDesc_tomarpedido_button)
        var volverButton = findViewById<Button>(R.id.pedidoDesc_volver_button)

        //----------------------- Muestro Datos -----------------------

        idUsuarioTextView.setText(" ID Usuario : " + pedido.getString("ped_userid") )
        precioTotalTextView.setText(" Precio Total : " + pedido.getString("ped_total") )
        dirOrigenTextView.setText(" Direccion Origin : " + pedido.getString("ped_direccioninicio") )
        dirDestinoEditText.setText(" Direccion Destino : " + pedido.getString("ped_direcciondestino") )

        //----------------------- Botones -----------------------

        tomarButton?.setOnClickListener {

            tomarPedidoFromServidor()
        }

        volverButton?.setOnClickListener {

            val intent:Intent = Intent(this,PedidoPendienteActivity::class.java)

            intent.putExtra("iduser",idUsuario)
            intent.putExtra("token",tokenUsario)
            intent.putExtra("pedidiosPendientes",pedidos)
            intent.putExtra("datos",datosUsuario)
            startActivity(intent)

            finish()
        }

    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun tomarPedidoFromServidor() {

        val url = config.URL.plus("/api/pedido/asignarPedidoADelivery/")

        val jsonObject2 = JSONObject()
        jsonObject2.put("idpedido",pedido.getString("ped_id") )
        jsonObject2.put("iddelivery",idUsuario)


        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: JsonObjectRequest( Request.Method.POST, url,jsonObject2,

            Response.Listener<JSONObject> { response ->

                var strResp = response.toString()

                /* NOTIFICAR AL CLIENTE QUE SU PEDIDO FUE TOMADO! ACA  !*/



                val intent:Intent = Intent(this,LoginDeliveryActivity::class.java)

                intent.putExtra("datos",datosUsuario)
                intent.putExtra("token",tokenUsario)

                startActivity(intent)

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
