package com.example.myapplicationfoodie

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class ProductoActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    //val urlServidor = "https://polar-stream-82449.herokuapp.com"
    val urlServidor = "http://192.168.0.4:5000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent
        val idComercio = objetoIntent.getIntExtra("idcomercio", 0)
        tokenUsario = objetoIntent.getStringExtra("token")

        //...........................................................................

        mensaje_Toast( "Desde pantalla productos "+ (idComercio.toString())  )

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

                mensaje_Toast("Recibi mensaje del servidor")

                var strResp = response.toString()
                val jsonob: JSONObject = JSONObject(strResp)

                create_buttons(jsonob)

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

    private fun create_buttons(jsonob: JSONObject) {

        mensaje_Toast("respuesta de rutas pedidos : "+jsonob.toString())

        var productos= jsonob.getJSONArray("data")

        var listaBotones = findViewById<LinearLayout>(R.id.productos_Layout)

        //....................Creacion de botones por comercios....................

        for (i in 0 until productos.length()) {

            val jsonObject1 = productos.getJSONObject(i)
            val value0 = jsonObject1.getString("prod_nombre")
            val value1 = jsonObject1.getString("prod_descripcion")
            val value2 = jsonObject1.getString("prod_value")


            val myButton = Button(this)

            myButton.setText(value0)
            myButton.setBackgroundColor(Color.parseColor("#18649E"))
            myButton.setTextColor(Color.parseColor("#F7F4F4"))
            myButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (24).toFloat())

            myButton?.setOnClickListener {

                // pantalla_productos( value6 )
            }


            listaBotones.addView(myButton)

        }


    }
}
