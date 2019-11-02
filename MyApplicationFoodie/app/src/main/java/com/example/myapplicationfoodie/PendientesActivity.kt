package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
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
    lateinit var pendientes: JSONArray



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


        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                val jsonob: JSONObject = JSONObject(response.toString())
                pendientes = jsonob.getJSONArray("pedidos")
                fillList()
                //mensaje_Toast(response.toString())

            },
            Response.ErrorListener {
                mensaje_Toast("Ocurrio un Error!")
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

    private fun fillList() {

        var listView = findViewById<ListView>(R.id.pendientes_listview)

        val list = ArrayList<String>()

        for (i in 0 until pendientes.length()) {

            val jsonObject1 = pendientes.getJSONObject(i)
            val value1 = jsonObject1.getInt("ped_id")
            val value2 = jsonObject1.getInt("ped_deliveryid")
            val value3 = jsonObject1.getInt("ped_estado")

            var mensaje:String = "Empty"

            if(value3==1){
                 mensaje = "IdPedido :${value1} IdDelivery :${value2} Estado : Pendiente "
            }else{
                 mensaje = "IdPedido :${value1} IdDelivery :${value2} Estado : Enviando"
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


            }
        }


    }
}
