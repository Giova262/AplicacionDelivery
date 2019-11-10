package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import org.json.JSONArray

class MisPedidosDeliveryActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    var datosUsuario :String = "-1"
    var idUsuario: Int = 0
    lateinit var pendientes: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pedidos_delivery)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("iduser",0)
        pendientes = JSONArray( objetoIntent.getStringExtra("pedidiosPendientes"))
        datosUsuario = objetoIntent.getStringExtra("datos")

        //....................Llenar lista.......................................

        fillList()

    }

    private fun fillList() {

        var listView = findViewById<ListView>(R.id.misPedidosDeliv_listview)

        val list = ArrayList<String>()

        for (i in 0 until pendientes.length()) {

            val jsonObject1 = pendientes.getJSONObject(i)
            val value1 = jsonObject1.getInt("ped_id")
            val value2 = jsonObject1.getInt("ped_deliveryid")
            val value3 = jsonObject1.getInt("ped_estado")

            var mensaje:String = "Empty"

            if(value3==1){
                mensaje = "IdPedido :${value1}  Estado : Pendiente "
            }else{
                mensaje = "IdPedido :${value1}  Estado : Enviando"
            }


            list.add(mensaje)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, list
        )

        listView.setAdapter(adapter)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {

                val jsonObject1 = pendientes.getJSONObject(position)

                pantalla_mipedidoopciones(jsonObject1.toString())

                //mensaje_Toast( position.toString() )
            }
        }


    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun pantalla_mipedidoopciones(datos: String) {
        val intent:Intent = Intent(this,PedidoOpcionesDeliveryActivity::class.java)

        intent.putExtra("token",tokenUsario)
        intent.putExtra("idUsuario",idUsuario)
        intent.putExtra("pedido",datos)
        intent.putExtra("datos",datosUsuario)

        startActivity(intent)
       // finish()
    }
}
