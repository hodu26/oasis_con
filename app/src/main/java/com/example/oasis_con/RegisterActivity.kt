package com.example.oasis_con

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var profileImageView: ImageView
    private var profileImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://hackathon-f01a9-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        storage = FirebaseStorage.getInstance("gs://hackathon-f01a9.appspot.com")

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        profileImageView = findViewById(R.id.profileImageView)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Firebase Storage에서 프로필 이미지 가져오기
        loadProfileImageFromFirebase()

        registerButton.setOnClickListener { registerUser() }
        loginButton.setOnClickListener { loginUser() }
    }

    private fun loadProfileImageFromFirebase() {
        val storageRef = storage.reference.child("profile_images/profile.png")

        // 이미지 다운로드 URL 가져오기
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            profileImageUrl = uri.toString()
            // Picasso 또는 Glide를 사용해 ImageView에 이미지 로드
            Picasso.get().load(profileImageUrl).into(profileImageView)
        }.addOnFailureListener {
            Toast.makeText(this, "이미지를 불러오는 데 실패했습니다: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            name.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    userId?.let {
                        saveUserData(userId, name, age, profileImageUrl!!)
                    }
                } else {
                    Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(userId: String, name: String, age: String, profileImageUrl: String) {
        val user = mapOf(
            "name" to name,
            "age" to age,
            "profileImageUrl" to profileImageUrl,
            "scrapList" to emptyList<String>() // 빈 스크랩 리스트
        )

        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "회원정보 저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginUser() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
