package com.example.myapplicationfoodie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import org.json.JSONObject


class ChatLogActivity : AppCompatActivity(){

    private var tokenUsario :String = "-1"
    private var datosUsuario :String = "-1"
    private var pedido: String = ""
    private var deliveryUid: String = ""
    private var usuarioUid: String = ""
    private var rol = -1

    var adapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)



        //----------------------- Recibo Datos -----------------------

        val objetoIntent : Intent =intent

        tokenUsario = objetoIntent.getStringExtra("token")
        pedido = objetoIntent.getStringExtra("pedido")
        datosUsuario = objetoIntent.getStringExtra("datos")


        var cliente = JSONObject(datosUsuario)
        usuarioUid = cliente.getString("uidfirebase")
        rol = cliente.getInt("rol")

        //------------------------------------------------------------

        var pedidoObj = JSONObject(pedido)

        var delivertId = -1

        if(rol==0){
            delivertId = pedidoObj.getInt("ped_deliveryid")
        }

        if(rol==1){
            delivertId = pedidoObj.getInt("ped_userid")
        }


        val url = config.URL.plus("/api/user/"+ delivertId.toString() )


        //----------------------- Mando al servidor -----------------------

        val queue = Volley.newRequestQueue( this )
        val jsonObjectRequest = object: StringRequest( Request.Method.GET, url,

            Response.Listener<String> { response ->

                val jsonob: JSONObject = JSONObject(response.toString())
               // mensaje_Toast(response.toString())

                deliveryUid = jsonob.getString("uidfirebase")


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


        //--------------- Botones ----------------------------------------------

        supportActionBar?.title = " Chat Log "

        ListenforMessages()

        recyclerview_chat_log.adapter = adapter

        recyclerview_chat_log.scrollToPosition( adapter.itemCount - 1 )

        enviar_boton_chat_log.setOnClickListener {


            preformSendMessage()

        }

    }

    private fun ListenforMessages(){

        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmessage = p0.getValue(ChatMessage::class.java)

                if(chatmessage!= null){


                        if(chatmessage.fromId == usuarioUid ){
                            adapter.add( ChatFromItem(chatmessage.text)  )

                        }else{
                            adapter.add( ChatToItem(chatmessage.text)  )
                        }

                }


            }

            override fun onCancelled(p0: DatabaseError) {
           }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
           }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })


    }

    class ChatMessage(val id:String, val text : String ,val fromId:String , val toId:String , val timestamp: Long ){
        constructor() :this( "","","","",-1)
    }

    private fun preformSendMessage(){

        //Aca envio mensaje a firebase

        val text = edittext_chat_log.text.toString()


        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!! ,text,usuarioUid!! ,deliveryUid,System.currentTimeMillis())

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                 Log.d("Main","Envie el mensaje a firebase")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition( adapter.itemCount - 1 )
            }
    }


    private fun setupDummyData(){
        var adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add( ChatFromItem("hoola") )
        adapter.add( ChatToItem("como") )
        adapter.add( ChatFromItem("sii") )
        adapter.add( ChatToItem("queee") )

        recyclerview_chat_log.adapter = adapter
    }

    private fun mensaje_Toast(s: String) {
        Toast.makeText( this,s, Toast.LENGTH_LONG).show()
    }
}



class ChatFromItem(val text:String ) : Item() {

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
    }
}

class ChatToItem(val text:String ) : Item() {

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
    }
}


