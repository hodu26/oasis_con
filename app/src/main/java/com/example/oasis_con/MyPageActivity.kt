package com.example.oasis_con

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class MyPageActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var profileName: TextView
    private lateinit var profileImage: ImageView
    private lateinit var editProfile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        profileName = findViewById(R.id.profile_name)
        profileImage = findViewById(R.id.profile_image)
        editProfile = findViewById(R.id.edit_profile)

        // 현재 사용자 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance("https://hackathon-f01a9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")

        if (userId != null) {
            // 이름 및 프로필 이미지 URL 불러오기
            database.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    profileName.text = name ?: "사용자 이름"

                    // 프로필 이미지 URL 가져오기
                    val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)
                    if (!profileImageUrl.isNullOrEmpty()) {
                        // Glide를 사용하여 프로필 이미지 로드
                        Glide.with(this@MyPageActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.profile) // 로딩 중 표시할 기본 이미지
                            .into(profileImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터베이스 에러 처리
                }
            })
        }

        // 프로필 수정 텍스트뷰 클릭 이벤트 처리
        editProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }
    }
}
