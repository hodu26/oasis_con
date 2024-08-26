package com.example.oasis_con

import com.example.oasis_con.model.*
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class BoardActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mStorage: FirebaseStorage
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        mDatabase = FirebaseDatabase.getInstance("https://hackathon-f01a9-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("posts")
        mStorage = FirebaseStorage.getInstance("gs://hackathon-f01a9.appspot.com")

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        val selectFileButton = findViewById<Button>(R.id.selectFileButton)
        val postButton = findViewById<Button>(R.id.postButton)

        selectFileButton.setOnClickListener { openFilePicker() }
        postButton.setOnClickListener { postContent() }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_SELECT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
        }
    }

    private fun postContent() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please enter a title and content", Toast.LENGTH_SHORT).show()
            return
        }

        val postId = mDatabase.push().key
        val post = Post(postId!!, title, content, null)

        if (selectedFileUri != null) {
            val fileRef = mStorage.reference.child("files/${selectedFileUri!!.lastPathSegment}")
            fileRef.putFile(selectedFileUri!!).addOnSuccessListener { taskSnapshot ->
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    post.fileUrl = uri.toString()
                    mDatabase.child(postId).setValue(post)
                }
            }
        } else {
            mDatabase.child(postId).setValue(post)
        }

        finish()
    }

    private fun updatePost(postId: String) {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please enter a title and content", Toast.LENGTH_SHORT).show()
            return
        }

        val post = Post(postId, title, content, null)

        if (selectedFileUri != null) {
            val fileRef = mStorage.reference.child("files/${selectedFileUri!!.lastPathSegment}")
            fileRef.putFile(selectedFileUri!!).addOnSuccessListener { taskSnapshot ->
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    post.fileUrl = uri.toString()
                    mDatabase.child(postId).setValue(post)
                }
            }
        } else {
            mDatabase.child(postId).setValue(post)
        }

        finish()
    }

    private fun deletePost(postId: String) {
        mDatabase.child(postId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to delete post: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sharePost(title: String, content: String, fileUrl: String?) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }

        if (fileUrl != null) {
            shareIntent.type = "image/*" // 이미지 파일일 경우
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileUrl))
        }

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }


    companion object {
        const val FILE_SELECT_CODE = 1
    }
}
