package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.json.JSONObject

class ProductoDescActivity : AppCompatActivity() {

    private lateinit var tokenUsario :String
    private var idComercio :Int = -1
    private var bolsaDeCompra = ArrayList<String>()
    private lateinit var producto: JSONObject
    private lateinit var datosUsuario:String
    private var idUsuario: Int = 0
    private var dirInicio: String = ""
    private var latInicio: Double = 0.0
    private var longInicio: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_desc)

        //----------------------- Recibir Datos -----------------------

        val objetoIntent : Intent =intent

        producto = JSONObject( objetoIntent.getStringExtra("producto") )
        tokenUsario = objetoIntent.getStringExtra("token")
        idComercio = objetoIntent.getIntExtra("idComercio", 0)
        bolsaDeCompra = objetoIntent.getStringArrayListExtra("bolsa")
        idUsuario = objetoIntent.getIntExtra("idUsuario",0)
        dirInicio  = objetoIntent.getStringExtra("dirInicio")
        latInicio  = objetoIntent.getDoubleExtra("latInicio",0.0)
        longInicio  = objetoIntent.getDoubleExtra("longInicio",0.0)
        datosUsuario = objetoIntent.getStringExtra("userData")

        //----------------------- Obtengo Elementos -----------------------

        val nombreTextView = findViewById<TextView>(R.id.productoDesc_nombreTextview)
        val precioTextView = findViewById<TextView>(R.id.productoDesc_precioTextview)
        val descripcionTextView = findViewById<TextView>(R.id.productoDesc_descripcionTextview)
        val cantidadTextView = findViewById<TextView>(R.id.productoDesc_cantTextview)
        val cantidadEditText = findViewById<EditText>(R.id.productoDesc_cantEditText)
        val agregarButton = findViewById<Button>(R.id.productoDesc_addButton)
        val volverButton = findViewById<Button>(R.id.productoDesc_volverButton)

        //----------------------- Muestro Datos -----------------------

        nombreTextView.setText( producto.getString("prod_nombre") )
        precioTextView.setText("Precio: " + producto.getString("prod_value") )
        descripcionTextView.setText( "Descripcion: " + producto.getString("prod_descripcion") )
        cantidadTextView.setText( "Cantidad de Unidades: " )

        //----------------------- Botones -----------------------

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
        intent.putExtra("dirInicio",dirInicio)
        intent.putExtra("latInicio",latInicio)
        intent.putExtra("longInicio",longInicio)
        intent.putExtra("idUsuario",idUsuario)
        intent.putExtra("userData",datosUsuario)

        startActivity(intent)
        finish()
    }
}
