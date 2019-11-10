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

    private var bolsaDeCompra = ArrayList<String>()
    private lateinit var datosUsuario:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bolsade_productos)

        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent
        bolsaDeCompra = objetoIntent.getStringArrayListExtra("bolsa")
        datosUsuario = objetoIntent.getStringExtra("userData")

        //----------------------- Lleno List-View -----------------------

        fillList()

    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun fillList() {

        //----------------------- Obtengo Elemento -----------------------

        var listView = findViewById<ListView>(R.id.bolsa_listview)

        //----------------------- Creo lista para el Adapter -----------------------

        val list = ArrayList<String>()

        for (i in 0 until bolsaDeCompra.size ) {

            val elementoBolsa: JSONObject = JSONObject( bolsaDeCompra.get(i) )
            val value0 = elementoBolsa.getInt("cantidad")
            val value1 = elementoBolsa.getString("nombre")

            list.add( value1 + "  -  "+ value0.toString() + "  Unidades" )
        }

        //----------------------- Creo el Adapter -----------------------

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, list
        )

        //----------------------- Seteo el Adapter -----------------------

        listView.setAdapter(adapter)

        //----------------------- OnClick de cada Item en la ListView -----------------------

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {


            }
        }

        //----------------------- End -----------------------

    }

}
