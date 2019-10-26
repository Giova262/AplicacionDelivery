package com.example.myapplicationfoodie

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import org.json.JSONArray

class ComercioActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    val urlServidor = "https://polar-stream-82449.herokuapp.com"
    //val urlServidor = "http://192.168.0.4:5000"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comercio)

        //.....................Recibo datos ....................................

         val objetoIntent : Intent =intent
         val comercios: JSONArray = JSONArray( objetoIntent.getStringExtra("datos") )
         tokenUsario = objetoIntent.getStringExtra("token")


        var listView = findViewById<ListView>(R.id.comercio_listview)

        //.........................................................................

        val list = ArrayList<String>()

        for (i in 0 until comercios.length()) {

            val jsonObject1 = comercios.getJSONObject(i)
            val value1 = jsonObject1.getString("com_nombre")

            list.add(value1)
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

                val jsonObject1 = comercios.getJSONObject(position)
                val value6 = jsonObject1.getInt("com_id")

                pantalla_productos( value6 )

            }

        }

    }


    private fun pantalla_productos( idcomercio :Int ) {

        val intent:Intent = Intent(this,ProductoActivity::class.java)
        intent.putExtra("idComercio",idcomercio)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("activdad","Comercio")
        startActivity(intent)
        //finish()
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }


}
