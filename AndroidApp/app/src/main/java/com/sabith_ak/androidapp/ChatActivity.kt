package com.sabith_ak.androidapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sabith_ak.androidapp.model.InitialData
import com.sabith_ak.androidapp.model.Message
import com.sabith_ak.androidapp.model.MessageType
import com.sabith_ak.androidapp.model.SendMessage
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import java.net.URISyntaxException

class ChatActivity : AppCompatActivity() {

    lateinit var mSocket: Socket
    lateinit var userName: String
    lateinit var roomId: String
    private val gson: Gson = Gson()

    lateinit var messageList: MutableList<Message>
    lateinit var chatBoxAdapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        userName = intent.extras!!.getString(MainActivity().nickName).toString()
        roomId = "room1"

        messageList = mutableListOf()
        rv_messagelist.layoutManager = LinearLayoutManager(this)
        rv_messagelist.itemAnimator = DefaultItemAnimator()
        chatBoxAdapter = ChatListAdapter(this, layoutInflater)
        rv_messagelist.adapter = chatBoxAdapter

        //connect you socket client to the server
        try {
            mSocket = IO.socket("http://add your local ip here:3000")

            //create connection
            mSocket.connect()
            // emit the event join along side with the username & room name
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on("newUserJoinedTheChat", onNewUser)
            mSocket.on("message", onUpdateChat)
            mSocket.on("userLeftChatRoom", onUserLeft)

        } catch (e: URISyntaxException) {
            e.printStackTrace();
        }

        btn_send_message.setOnClickListener {
            if (mSocket.connected()) {
                if (et_message.text.toString().trim().isNotEmpty()) {
                    sendMessage(et_message.text.toString())
                }
            } else {
                Toast.makeText(this, "Issue with socket connection!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendMessage(content: String) {
        val sendData = SendMessage(
            userName,
            content,
            roomId
        )
        val jsonSendData = gson.toJson(sendData)
        mSocket.emit("newMessage", jsonSendData)

        val message = Message(
            userName,
            content,
            roomId,
            MessageType.CHAT_MINE.index
        )
        addItemToRecyclerView(message)
    }

    private fun addItemToRecyclerView(message: Message) {
        Log.e("UserType", message.viewType.toString())
        //Since this function is inside of the listener,
        // You need to do it on UIThread!
        runOnUiThread {
            try {
                chatBoxAdapter.addItem(message)
                et_message.setText("")
                //move focus on last message
                rv_messagelist.smoothScrollToPosition(chatBoxAdapter.itemCount - 1)
            } catch (e: Exception){ }
        }
    }

    private var onConnect =  Emitter.Listener {
        val data = InitialData(userName, roomId)
        val jsonData = gson.toJson(data)
        mSocket.emit("join", jsonData)

    }

    private var onNewUser = Emitter.Listener {
        Log.d("ChatBox", "on New User triggered.")
        val name = it[0] as String
        val chat = Message(name, "", roomId, MessageType.USER_JOIN.index)
        addItemToRecyclerView(chat)
    }

    private var onUpdateChat = Emitter.Listener {
        Log.d("ChatBox", "on New Message received.")
        val message = gson.fromJson(it[0].toString(), Message::class.java)
        message.viewType = MessageType.CHAT_PARTNER.index
        addItemToRecyclerView(message)
    }

    private var onUserLeft = Emitter.Listener {
        val leftUserName = it[0] as String
        val chat: Message = Message(leftUserName, "", "", MessageType.USER_LEAVE.index)
        addItemToRecyclerView(chat)
    }

    override fun onDestroy() {
        val data = InitialData(userName, roomId)
        val jsonData = gson.toJson(data)
        mSocket.emit("unSubscribe", jsonData)
        mSocket.disconnect()
        super.onDestroy()
    }

}