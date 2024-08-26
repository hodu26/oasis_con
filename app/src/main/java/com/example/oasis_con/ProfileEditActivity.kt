package com.example.oasis_con

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileEditActivity : AppCompatActivity() {

    private var profileImageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var editProfileName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // XML 레이아웃 파일에서 참조할 수 있는 View 요소들을 초기화합니다.
        val profileImageEdit = findViewById<ImageView>(R.id.profile_image_edit)
        editProfileName = findViewById(R.id.edit_profile_name)
        val clearNameButton = findViewById<ImageButton>(R.id.clear_name_button)
        val saveChangesButton = findViewById<Button>(R.id.save_changes_button)

        // 현재 사용자 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance("https://hackathon-f01a9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")

        if (userId != null) {
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                    editProfileName.hint = name ?: "사용자 이름"

                    if (!profileImageUrl.isNullOrEmpty()) {
                        // Glide 라이브러리를 사용해 이미지 로드 (Gradle에 Glide 추가 필요)
                        Glide.with(this@ProfileEditActivity)
                            .load(profileImageUrl)
                            .apply(RequestOptions.circleCropTransform())  // 이미지 원형으로 변환
                            .into(profileImageEdit)
                    } else {
                        profileImageEdit.setImageResource(R.drawable.profile) // 기본 이미지 설정
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileEditActivity, "사용자 정보를 불러오는 데 실패했습니다: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 프로필 이미지 클릭 시 이미지 선택
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                profileImageUri = uri
                profileImageEdit.setImageURI(uri)
            }
        }

        profileImageEdit.setOnClickListener {
            pickImage.launch("image/*")
        }

        // '수정 완료' 버튼 클릭 이벤트 처리
        saveChangesButton.setOnClickListener {
            val name = editProfileName.text.toString()
            if (userId != null) {
                if (profileImageUri != null) {
                    uploadImageToStorage(userId, name, profileImageUri!!)
                } else {
                    saveUserData(userId, name, "")
                }
            } else {
                Toast.makeText(this, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 'clear_name_button' 클릭 이벤트: 이름 입력 필드 초기화
        clearNameButton.setOnClickListener {
            editProfileName.text.clear()
        }
    }


    // 이미지를 Firebase Storage에 업로드하는 함수
    private fun uploadImageToStorage(userId: String, name: String, imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance("gs://hackathon-f01a9.appspot.com").reference.child("profile_images/$userId/${UUID.randomUUID()}.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveUserData(userId, name, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "이미지 업로드에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 계정 정보를 Firebase Realtime Database에 저장하는 함수
    private fun saveUserData(userId: String, name: String, profileImageUrl: String) {
        val user = mapOf(
            "name" to name,
            "profileImageUrl" to profileImageUrl,
            "scrapList" to emptyList<String>() // 빈 스크랩 리스트
        )

        val database = FirebaseDatabase.getInstance("https://hackathon-f01a9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userRef = database.getReference("users").child(userId)

        userRef.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                // 프로필 수정이 완료되면 MyPageActivity로 이동
                val intent = Intent(this, ProfileFragment::class.java)
                startActivity(intent)
                finish() // 현재 액티비티를 종료하여 백 스택에서 제거
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "프로필 수정에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
