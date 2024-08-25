package com.example.oasis_con

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private val messages = mutableListOf<String>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mDatabase = FirebaseDatabase.getInstance("https://hackathon-f01a9-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chats")

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        val sendButton = findViewById<Button>(R.id.sendButton)

        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener { sendMessage() }

        listenForMessages()
    }

    private fun sendMessage() {
        val message = messageEditText.text.toString()

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        val messageMap = HashMap<String, String>()
        messageMap["message"] = message

        mDatabase.push().setValue(messageMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                messageEditText.setText("") // 입력 필드를 초기화
            } else {
                Toast.makeText(this, "Message sending failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun listenForMessages() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (child in snapshot.children) {
                    val message = child.child("message").getValue(String::class.java)
                    message?.let { messages.add(it) }
                }
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Failed to load messages.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
