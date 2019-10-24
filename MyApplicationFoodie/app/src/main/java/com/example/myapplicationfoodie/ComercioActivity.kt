package com.example.myapplicationfoodie

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import org.json.JSONArray

class ComercioActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    //val urlServidor = "https://polar-stream-82449.herokuapp.com"
    val urlServidor = "http://192.168.0.4:5000"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comercio)

        //.....................Recibo datos ....................................

         val objetoIntent : Intent =intent
         val datos: JSONArray = JSONArray( objetoIntent.getStringExtra("datos") )
         tokenUsario = objetoIntent.getStringExtra("token")


        //.........................................................................


        //.....................Obtengo elementos..................................

        var listaBotones = findViewById<LinearLayout>(R.id.comercios_Layout)

        //........................................................................


        //....................Creacion de botones por comercios....................

        for (i in 0 until datos.length()) {

            val jsonObject1 = datos.getJSONObject(i)
            val value0 = jsonObject1.getString("com_nombre")
            val value1 = jsonObject1.getString("com_direccion")
            val value2 = jsonObject1.getString("com_descripcion")
            val value3 = jsonObject1.getDouble("com_latitud")
            val value4 = jsonObject1.getDouble("com_longitud")
            val value5 = jsonObject1.getInt("com_estado")
            val value6 = jsonObject1.getInt("com_id")

            val myButton = Button(this)

            myButton.setText(value0)
            myButton.setBackgroundColor(Color.parseColor("#18649E"))
            myButton.setTextColor(Color.parseColor("#F7F4F4"))
            myButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (24).toFloat() )

            myButton?.setOnClickListener {

                pantalla_productos( value6 )
            }


            listaBotones.addView(myButton)

        }
    }


    private fun pantalla_productos( datos :Int ) {

       // mensaje_Toast( datos.toString() )

        val intent:Intent = Intent(this,ProductoActivity::class.java)
        intent.putExtra("idcomercio",datos)
        intent.putExtra("token",tokenUsario)
        startActivity(intent)
        //finish()
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }


}
