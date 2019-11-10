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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class HistorialActivity : AppCompatActivity() {

    private var tokenUsario :String = "-1"
    private var idUsuario: Int = 0
    private lateinit var pendientes: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario =  objetoIntent.getIntExtra("iduser",0)


        //----------------------- Conecto con el Servidor -----------------------

        getHistorialFromServidor()

    }

    private fun getHistorialFromServidor() {

        val url = config.URL.plus("/api/pedido/getPedidosUsuario/"+ idUsuario.toString() )

        val objetoJson= JSONObject()
        objetoJson.put("iduser", idUsuario )

        //----------------------- Mando al servidor -----------------------

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                val jsonob: JSONObject = JSONObject(response.toString())
                pendientes = jsonob.getJSONArray("pedidos")

                //----------------------- Lleno la ListView -----------------------
                fillList()

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

        var listView = findViewById<ListView>(R.id.historial_listview)

        val list = ArrayList<String>()

        var mensaje:String

        for (i in 0 until pendientes.length()) {

            val jsonObject1 = pendientes.getJSONObject(i)
            val value1 = jsonObject1.getInt("ped_id")
            val value2 = jsonObject1.getInt("ped_deliveryid")
            val value3 = jsonObject1.getInt("ped_estado")

            if(value3==3){
                mensaje = "IdPedido: ${value1} IdDelivery: ${value2} Estado: Entregado "
                list.add(mensaje)
            }

        }

        if( list.size == 0 ) mensaje_Toast("No tienes Historal para mostrar!")

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
