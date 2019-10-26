package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.json.JSONObject

class ProductoDescActivity : AppCompatActivity() {

    lateinit var tokenUsario :String
    var idComercio :Int = -1
    var bolsaDeCompra = ArrayList<String>()
    lateinit var producto: JSONObject

    val urlServidor = "https://polar-stream-82449.herokuapp.com"
    //val urlServidor = "http://192.168.0.4:5000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_desc)

        //.....................Recibo datos ....................................

        val objetoIntent : Intent =intent

        producto = JSONObject( objetoIntent.getStringExtra("producto") )
        tokenUsario = objetoIntent.getStringExtra("token")
        idComercio = objetoIntent.getIntExtra("idComercio", 0)
        bolsaDeCompra = objetoIntent.getStringArrayListExtra("bolsa")


        //...........Obtengo Elementos.............................................

        var nombreTextView = findViewById<TextView>(R.id.productoDesc_nombreTextview)
        var precioTextView = findViewById<TextView>(R.id.productoDesc_precioTextview)
        var descripcionTextView = findViewById<TextView>(R.id.productoDesc_descripcionTextview)
        var cantidadTextView = findViewById<TextView>(R.id.productoDesc_cantTextview)
        var cantidadEditText = findViewById<EditText>(R.id.productoDesc_cantEditText)
        var agregarButton = findViewById<Button>(R.id.productoDesc_addButton)
        var volverButton = findViewById<Button>(R.id.productoDesc_volverButton)

        //........................................................................

        nombreTextView.setText( producto.getString("prod_nombre") )
        precioTextView.setText("Precio: " + producto.getString("prod_value") )
        descripcionTextView.setText( "Descripcion: " + producto.getString("prod_descripcion") )
        cantidadTextView.setText( "Cantidad de Unidades: " )

        //...........................................................................

        agregarButton?.setOnClickListener {

            if( cantidadEditText.text.isNotEmpty() ){

                val newItem= JSONObject()
                newItem.put("id", producto.getString("prod_id") )
                newItem.put("cantidad", cantidadEditText.text )
                newItem.put("nombre", producto.getString("prod_nombre") )

                bolsaDeCompra.add( newItem.toString() )

                pantalla_productos()

            }else{
                mensaje_Toast( "Cantidad Invalida!" )
            }

        }

        volverButton?.setOnClickListener {
                pantalla_productos()
        }

    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun pantalla_productos( ) {

        val intent:Intent = Intent(this,ProductoActivity::class.java)

        intent.putExtra("idComercio",idComercio)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("activdad","ProductosDesc")
        intent.putExtra("bolsa",bolsaDeCompra)
        startActivity(intent)
        finish()
    }
}
