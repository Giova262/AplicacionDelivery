package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import org.json.JSONObject

class PedidoOpcionesDeliveryActivity : AppCompatActivity() {


    private var tokenUsario :String = "-1"
    private var datosUsuario :String = "-1"
    private var idUsuario: Int = 0
    private var pedido: String = ""
    private var latitud:Double = 0.0
    private var longitud:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_opciones_delivery)

        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("idUsuario",0)
        pedido = objetoIntent.getStringExtra("pedido")
        datosUsuario = objetoIntent.getStringExtra("datos")

        //----------------------- Obtengo Lat/Long -----------------------

        var pedidoObj = JSONObject(pedido)
        latitud = pedidoObj.getDouble("ped_latituddestino")
        longitud = pedidoObj.getDouble("ped_longituddestino")

        //----------------------- Obtengo Elementos -----------------------

        var mapaBoton = findViewById<Button>(R.id.pedOpcDelivery_mapaButton)
        var chatBoton = findViewById<Button>(R.id.pedOpcDelivery_chatButton)


        //----------------------- Botones -----------------------

        mapaBoton?.setOnClickListener {
            pantalla_mapa( )
        }

        chatBoton?.setOnClickListener {
            performRegister( )
        }

    }

    private fun performRegister(){

        val intent:Intent = Intent(this,ChatLogActivity::class.java)

        intent.putExtra("token",tokenUsario)
        intent.putExtra("pedido",pedido)
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
