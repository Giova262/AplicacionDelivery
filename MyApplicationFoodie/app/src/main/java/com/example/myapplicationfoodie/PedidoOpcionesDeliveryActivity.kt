package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class PedidoOpcionesDeliveryActivity : AppCompatActivity() {


    var tokenUsario :String = "-1"
    var datosUsuario :String = "-1"
    var idUsuario: Int = 0
    var pedido: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_opciones_delivery)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("idUsuario",0)
        pedido = objetoIntent.getStringExtra("pedido")
        datosUsuario = objetoIntent.getStringExtra("datos")

        //.........................................

        mensaje_Toast( pedido )
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }
}
