package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import org.json.JSONObject

class BolsadeProductosActivity : AppCompatActivity() {

    var bolsaDeCompra = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bolsade_productos)

        //...................Recibo datos.......................

        val objetoIntent : Intent =intent
        bolsaDeCompra = objetoIntent.getStringArrayListExtra("bolsa")


        //...........................................................

        fillList()


    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }


    private fun fillList() {

        var listView = findViewById<ListView>(R.id.bolsa_listview)

        val list = ArrayList<String>()

        for (i in 0 until bolsaDeCompra.size ) {

            val elementoBolsa: JSONObject = JSONObject( bolsaDeCompra.get(i) )
            val value0 = elementoBolsa.getInt("cantidad")
            val value1 = elementoBolsa.getString("nombre")

            list.add( value1 + "  -  "+ value0.toString() + "  Unidades" )
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
