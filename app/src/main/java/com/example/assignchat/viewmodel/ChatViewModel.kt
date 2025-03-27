package com.example.assignchat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignchat.model.Message
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatViewModel : ViewModel() {
    private val database = Firebase.database.reference.child("messages")
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    init {
        database.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<Message>()
                for (data in snapshot.children) {
                    val message = data.getValue(Message::class.java)
                    message?.let { messageList.add(it) }
                }
                _messages.value = messageList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (e.g., log it)
                Log.e("ChatViewModel", "Database error: ${error.message}")
            }
        })
    }

    fun sendMessage(sender: String, content: String) {
        val message = Message(sender, content, System.currentTimeMillis())
        database.push().setValue(message)
    }
}