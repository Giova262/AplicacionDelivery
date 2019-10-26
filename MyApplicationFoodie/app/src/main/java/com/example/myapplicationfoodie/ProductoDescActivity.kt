package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.json.JSONObject

class ProductoDescActivity : AppCompatActivity() {

    var tokenUsario :String = "-1"
    val urlServidor = "https://polar-stream-82449.herokuapp.com"
    //val urlServidor = "http://192.168.0.4:5000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_desc)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent
        val producto: JSONObject = JSONObject( objetoIntent.getStringExtra("producto") )
        tokenUsario = objetoIntent.getStringExtra("token")

        //...........Obtengo Elementos.............................................

        var nombreTextView = findViewById<TextView>(R.id.productoDesc_nombreTextview)
        var precioTextView = findViewById<TextView>(R.id.productoDesc_precioTextview)
        var descripcionTextView = findViewById<TextView>(R.id.productoDesc_descripcionTextview)
        var cantidadTextView = findViewById<TextView>(R.id.productoDesc_cantTextview)
        var cantidadEditText = findViewById<EditText>(R.id.productoDesc_cantEditText)
        var agregarButton = findViewById<Button>(R.id.productoDesc_addButton)

        //........................................................................

        nombreTextView.setText( producto.getString("prod_nombre") )
        precioTextView.setText("Precio: " + producto.getString("prod_value") )
        descripcionTextView.setText( "Descripcion: " + producto.getString("prod_descripcion") )
        cantidadTextView.setText( "Cantidad de Unidades: " )

        //mensaje_Toast( producto.toString() )

    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }
}
