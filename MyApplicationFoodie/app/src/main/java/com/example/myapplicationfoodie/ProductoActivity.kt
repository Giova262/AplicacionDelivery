package com.example.myapplicationfoodie

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.widget.AdapterView.OnItemClickListener
import android.R.string.ok
import android.R.string.no
import android.R.attr.label
import android.os.Parcel
import android.os.Parcelable
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup



class ProductoActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    val urlServidor = "https://polar-stream-82449.herokuapp.com"
    //val urlServidor = "http://192.168.0.4:5000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent
        val idComercio = objetoIntent.getIntExtra("idcomercio", 0)
        tokenUsario = objetoIntent.getStringExtra("token")

        //...........................................................................

        enviarDatosAlServidor(idComercio)

    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun enviarDatosAlServidor( idcomecio: Int ) {


        val url = urlServidor.plus("/api/producto/productosPorComercio/"+ idcomecio.toString() )


        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)

                fillList(jsonob)

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


    private fun fillList(jsonob: JSONObject) {


        var productos= jsonob.getJSONArray("data")

        var listView = findViewById<ListView>(R.id.producto_listview)

        val list = ArrayList<String>()

        for (i in 0 until productos.length()) {

            val jsonObject1 = productos.getJSONObject(i)
            val value0 = jsonObject1.getString("prod_nombre")

            list.add(value0)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, list
        )

        listView.setAdapter(adapter)

        listView.onItemClickListener = object : OnItemClickListener {

            override fun onItemClick(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {

                val jsonObject1 = productos.getJSONObject(position)

                pantalla_productosDec(jsonObject1.toString())


            }

        }

    }

    private fun pantalla_productosDec( datos :String ) {

        val intent:Intent = Intent(this,ProductoDescActivity::class.java)
        intent.putExtra("producto",datos)
        intent.putExtra("token",tokenUsario)
        startActivity(intent)
        //finish()
    }
}






