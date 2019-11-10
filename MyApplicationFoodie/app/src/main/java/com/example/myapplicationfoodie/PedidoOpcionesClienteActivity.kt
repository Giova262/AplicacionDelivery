package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class PedidoOpcionesClienteActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    var datosUsuario :String = "-1"
    var idUsuario: Int = 0
    var pedido: String = ""

    var latitud:Double = 0.0
    var longitud:Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_opciones_cliente)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("idUsuario",0)
        pedido = objetoIntent.getStringExtra("pedido")
        datosUsuario = objetoIntent.getStringExtra("datos")

        //.........................................

        var pedidoObj = JSONObject(pedido)

        latitud = pedidoObj.getDouble("ped_latituddestino")
        longitud = pedidoObj.getDouble("ped_longituddestino")

       // mensaje_Toast( pedido )

        //..............Obtengo elementos.............................

        var mapaBoton = findViewById<Button>(R.id.pedOpcCliente_mapabutton)
        var chatBoton = findViewById<Button>(R.id.pedOpcCliente_chatbutton)
        var entregadoBoton = findViewById<Button>(R.id.pedOpcCliente_entregadobutton)
        var volverBoton = findViewById<Button>(R.id.pedOpcCliente_volverbutton)

        //..............Botones..................................

        mapaBoton?.setOnClickListener {
            pantalla_mapa()
        }

    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun pantalla_mapa( ) {
        val intent:Intent = Intent(this,MapsActivity::class.java)

        intent.putExtra("lat",latitud)
        intent.putExtra("long",longitud)

        startActivity(intent)
        //finish()
    }
}
