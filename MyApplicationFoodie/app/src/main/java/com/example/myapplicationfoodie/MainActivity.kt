package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import com.android.volley.Response




class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {


    private var callbackManager: CallbackManager? = null
    private val TAG = "FaceLog"
    private lateinit var auth: FirebaseAuth
    private var tokenRedSocial : String? = null
    private var rolDelUsuario = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //----------------------- Initialize FIRE-BASE Auth -----------------------

        auth = FirebaseAuth.getInstance()

        //----------------------- Initialize Facebook Login button -----------------------

        callbackManager = CallbackManager.Factory.create()

        //----------------------- Get Elements -----------------------

        val emailEditText = findViewById<EditText>( R.id.main_emailEditText )
        val passEditText = findViewById<EditText>( R.id.main_passEditText )
        val buttonFacebookLogin = findViewById<LoginButton>(R.id.login_button)
        val buttonRegistrar = findViewById<Button>(R.id.main_registrarBoton)
        val buttonEntrar = findViewById<Button>(R.id.main_entrarBoton)
        val spinner: Spinner = findViewById(R.id.main_spinner)

        //----------------------- Spinner -----------------------

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

        //----------------------- Buttons -----------------------
        
        buttonEntrar?.setOnClickListener {

            var mail = emailEditText.text
            var pass = passEditText.text
            var rol = rolDelUsuario
            var uidfirebase = "-1"

            val jsonObject = JSONObject()
            jsonObject.put("mail",mail)
            jsonObject.put("pass",pass)
            jsonObject.put("rol",rol)
            jsonObject.put("idToken",uidfirebase)

            loginToServer(jsonObject)
        }

        buttonRegistrar?.setOnClickListener {
            pantalla_registro()
        }

        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {

                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {

                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {

                Log.d(TAG, "facebook:onError", error)
            }
        })

        //-----------------------  End  -----------------------

    }

    private fun loginToServer(jsonObject: JSONObject) {

        //----------------------- URL -----------------------

        val url = config.URL.plus("/api/user/login")

        //----------------------- Send data to Server -----------------------

        val queue = Volley.newRequestQueue( this )

        val jsonObjectRequest = JsonObjectRequest(url, jsonObject,

            Response.Listener { response ->

                var strResp = response.toString()

                val jsonob: JSONObject = JSONObject(strResp)
                val mensaje = jsonob.getString("message")
                val status = jsonob.getInt("status")
                tokenRedSocial = jsonob.getString("token")

                if(status == 1){

                    val datos = jsonob.getJSONObject("dato")

                    if( rolDelUsuario == 0){
                        pantalla_loginCliente( datos.toString() )
                    }
                    else if( rolDelUsuario == 1){
                        pantalla_loginDelivery( datos.toString() )
                    }else{
                        mensaje_Toast("Rol ${rolDelUsuario} no soportado por la app ")
                    }

                }else mensaje_Toast(mensaje)

            },
            Response.ErrorListener { error ->

                mensaje_Toast( "Error en respuesta del Servidor!" )
                error.printStackTrace()
            }
        )

        queue.add( jsonObjectRequest )
    }

    private fun pantalla_loginDelivery(datos: String) {
        val intent:Intent = Intent(this,LoginDeliveryActivity::class.java)
        intent.putExtra("datos",datos)
        intent.putExtra("token",tokenRedSocial)
        startActivity(intent)
        finish()
    }

    private fun pantalla_registro() {
        val intent:Intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun pantalla_loginCliente(datos :String ) {
        val intent:Intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("datos",datos)
        intent.putExtra("token",tokenRedSocial)
        startActivity(intent)
        finish()
    }

    private fun pantalla_pruebas() {
        val intent:Intent = Intent(this,MapsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s,Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //----------------------- Pass the activity result back to the Facebook SDK -----------------------
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    public override fun onStart() {

        super.onStart()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        //----------------------- Verifico Token de Facebook usando Firebase -----------------------

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //----------------------- Obtengo el Token de Firebase -----------------------

                    Log.d(TAG, "signInWithCredential:success")

                    val user = auth.currentUser
                    user!!.getIdToken(true).addOnCompleteListener(this) { task2 ->

                            if (task2.isSuccessful) {

                                // ----------------------- Envio Token de Fire-base al Servidor -----------------------

                                val idToken = task2.result?.token

                                val jsonObject2 = JSONObject()
                                jsonObject2.put("mail","none")
                                jsonObject2.put("pass","none")
                                jsonObject2.put("rol",rolDelUsuario )
                                jsonObject2.put("idToken",idToken)

                                loginToServer(jsonObject2)

                            } else {

                                mensaje_Toast("Error Token de Fire-base")
                            }
                        }

                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()

                }
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        rolDelUsuario = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}
