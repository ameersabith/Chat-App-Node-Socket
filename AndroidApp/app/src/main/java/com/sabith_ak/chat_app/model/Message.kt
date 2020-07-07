package com.sabith_ak.chat_app.model

data class Message(
        val name: String = "",
        val messageContent: String = "",
        val roomId: String = "",
        var viewType : Int = 0
)

data class InitialData(
        val userName : String,
        val roomId : String
)

data class SendMessage(
        val userName : String,
        val messageContent: String,
        val roomId: String
)