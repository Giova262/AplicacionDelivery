package com.example.myapplicationfoodie


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.widget.AdapterView.OnItemClickListener
import android.location.Address
import android.location.Geocoder
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray


class ProductoActivity : AppCompatActivity() {

    private var tokenUsario :String = "-1"
    private var idComercio :Int = -1
    private var idUsuario: Int = 0
    private var dirInicio: String = ""
    private var latInicio: Double = 0.0
    private var longInicio: Double = 0.0
    private lateinit var datosUsuario:String
    private var bolsaDeCompra = ArrayList<String>()
    private lateinit var productos :JSONArray
    private var coder = Geocoder(this)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        //----------------------- Recibir Datos -----------------------

        val objetoIntent : Intent =intent

        if( objetoIntent.getStringExtra("activdad") == "ProductosDesc" ){

            bolsaDeCompra = objetoIntent.getStringArrayListExtra("bolsa")

        }else{

            bolsaDeCompra.clear()

        }

        idComercio = objetoIntent.getIntExtra("idComercio", 0)
        tokenUsario = objetoIntent.getStringExtra("token")
        idUsuario = objetoIntent.getIntExtra("idUsuario",0)
        dirInicio  = objetoIntent.getStringExtra("dirInicio")
        latInicio  = objetoIntent.getDoubleExtra("latInicio",0.0)
        longInicio  = objetoIntent.getDoubleExtra("longInicio",0.0)
        datosUsuario = objetoIntent.getStringExtra("userData")


        //----------------------- Obtengo Elementos -----------------------

        var verBolsaButton = findViewById<Button>(R.id.producto_verbolsaButton)
        var confirmarButton = findViewById<Button>(R.id.producto_confirmarButton)


        //----------------------- Botones -----------------------

        verBolsaButton?.setOnClickListener {
            pantalla_bolsaDeProductos()
        }

        confirmarButton?.setOnClickListener {
            confirmar_compra()
        }

        //----------------------- Enviar Datos -----------------------

        getComercioProductsFromServer(idComercio)

    }

    private fun confirmar_compra() {

        var list = JSONArray()

        for (i in 0 until bolsaDeCompra.size) {

            val jsonob: JSONObject = JSONObject(bolsaDeCompra[i])
            list.put(jsonob)
        }

        //----------------------- Obtengo Elementos -----------------------

        var direccionEdittext = findViewById<EditText>(R.id.producto_direccionEditText)
        var direccionString = direccionEdittext.text

        //----------------------- Checkeo Direccion -----------------------

        if( direccionString.isNotEmpty()){

            //----------------------- Obtengo Lat/Long con GeoLocation -----------------------

            var address:List<Address> = coder.getFromLocationName( direccionString.toString() ,5)

            if (address.isNotEmpty()) {

                val latitude = address.get(0).getLatitude()
                val longitude = address.get(0).getLongitude()

                val objetoJson= JSONObject()
                objetoJson.put("iduser", idUsuario )
                objetoJson.put("diri", dirInicio )
                objetoJson.put("dirf", direccionString )
                objetoJson.put("lati", latInicio )
                objetoJson.put("longi", longInicio )
                objetoJson.put("latf", latitude )
                objetoJson.put("longf", longitude )
                objetoJson.put("items", list )


                //----------------------- Mando al Servidor -----------------------

                val url = config.URL.plus("/api/pedido/registrarPedido" )


                val queue = Volley.newRequestQueue( this )
                val jsonObjectRequest = object: JsonObjectRequest( Request.Method.POST, url,objetoJson,

                    Response.Listener<JSONObject> { response ->

                        mensaje_Toast("Tu Pedido realizado Exitosamente! ")
                        pantalla_login()

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

        }else{
            mensaje_Toast( "Ingresa tu Direccion Primero!" )
        }


    }

    private fun pantalla_login() {

        val intent:Intent = Intent(this,LoginActivity::class.java)

        intent.putExtra("token",tokenUsario)
        intent.putExtra("datos",datosUsuario)

        startActivity(intent)
    }

    private fun pantalla_bolsaDeProductos() {

        val intent:Intent = Intent(this,BolsadeProductosActivity::class.java)

        intent.putExtra("bolsa",bolsaDeCompra)
        intent.putExtra("userData",datosUsuario)

        startActivity(intent)
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }

    private fun getComercioProductsFromServer(idcomecio: Int ) {

        val url = config.URL.plus("/api/producto/productosPorComercio/"+ idcomecio.toString() )


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


        productos= jsonob.getJSONArray("data")

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

    private fun pantalla_productosDec( productos :String ) {

        val intent:Intent = Intent(this,ProductoDescActivity::class.java)

        intent.putExtra("producto",productos)
        intent.putExtra("token",tokenUsario)
        intent.putExtra("idComercio",idComercio)
        intent.putExtra("bolsa",bolsaDeCompra)
        intent.putExtra("dirInicio",dirInicio)
        intent.putExtra("latInicio",latInicio)
        intent.putExtra("longInicio",longInicio)
        intent.putExtra("idUsuario",idUsuario)
        intent.putExtra("userData",datosUsuario)

        startActivity(intent)

    }
}






