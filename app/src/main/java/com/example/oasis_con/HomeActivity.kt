package com.example.oasis_con

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val boardButton = findViewById<Button>(R.id.boardButton)
        val chatButton = findViewById<Button>(R.id.chatButton)
        val mapButton = findViewById<Button>(R.id.mapButton)

        logoutButton.setOnClickListener { logout() }
        boardButton.setOnClickListener { openBoard() }
        chatButton.setOnClickListener { openChat() }
        mapButton.setOnClickListener { openMap() }
    }

    private fun logout() {
        mAuth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openBoard() {
        val intent = Intent(this, BoardActivity::class.java)
        startActivity(intent)
    }

    private fun openChat() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun openMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}
