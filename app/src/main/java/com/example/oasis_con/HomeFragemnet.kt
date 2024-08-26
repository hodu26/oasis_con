package com.example.oasis_con

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        mAuth = FirebaseAuth.getInstance()

        // 버튼들을 찾아서 변수에 할당
        //
        //val boardButton: Button = view.findViewById(R.id.boardButton)
        //val chatButton: Button = view.findViewById(R.id.chatButton)
        //val mapButton: Button = view.findViewById(R.id.mapButton)
        val gameButton: Button = view.findViewById(R.id.gameButton)
<<<<<<< HEAD
        val apiButton: Button = view.findViewById(R.id.ApiButton)
        val infoButton: Button = view.findViewById(R.id.InfoButton)
        val mypageButton: Button = view.findViewById(R.id.MypageButton)
=======
        //val apiButton: Button = view.findViewById(R.id.ApiButton)
>>>>>>> 0c28255ea8fa03b9fb87479bb6d7451bc54d9677

        // 각 버튼에 클릭 리스너 설정
        //logoutButton.setOnClickListener { onLogoutClicked() }
        //boardButton.setOnClickListener { onBoardClicked() }
        //chatButton.setOnClickListener { onChatClicked() }
        //mapButton.setOnClickListener { onMapClicked() }
        gameButton.setOnClickListener { onGameClicked() }
<<<<<<< HEAD
        apiButton.setOnClickListener { onApiClicked() }
        infoButton.setOnClickListener { onInfoClicked() }
        mypageButton.setOnClickListener { onMypageClicked() }
=======
        //apiButton.setOnClickListener { onApiClicked() }
>>>>>>> 0c28255ea8fa03b9fb87479bb6d7451bc54d9677

        return view
    }

    private fun onLogoutClicked() {
        // 로그아웃 로직 구현
        mAuth.signOut()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
//        finish()
    }
/*
    private fun onBoardClicked() {
        // BoardActivity로 전환
        val intent = Intent(activity, BoardActivity::class.java)
        startActivity(intent)
    }

    private fun onChatClicked() {
        // 채팅 화면으로 이동하는 로직 구현
        Toast.makeText(context, "Chat clicked", Toast.LENGTH_SHORT).show()
    }

    private fun onMapClicked() {
        // 지도 화면으로 이동하는 로직 구현
        Toast.makeText(context, "Map clicked", Toast.LENGTH_SHORT).show()
    }
*/
    private fun onGameClicked() {
        // 게임 화면으로 이동하는 로직 구현
        val intent = Intent(activity, GameActivity::class.java)
        startActivity(intent)
    }
/*
    private fun onApiClicked() {
        // API 관련 기능을 실행하는 로직 구현
<<<<<<< HEAD
        Toast.makeText(context, "API clicked", Toast.LENGTH_SHORT).show()
    }

    private fun onInfoClicked() {
        val intent = Intent(activity, InfoActivity::class.java)
        startActivity(intent)
    }

    private fun onMypageClicked() {
        val intent = Intent(activity, MyPageActivity::class.java)
        startActivity(intent)
    }
=======
        val intent = Intent(activity, ItemActivity::class.java)
        startActivity(intent)
    }*/
>>>>>>> 0c28255ea8fa03b9fb87479bb6d7451bc54d9677
}