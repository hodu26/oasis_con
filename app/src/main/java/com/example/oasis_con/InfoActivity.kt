package com.example.oasis_con

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info) // XML 레이아웃 파일 연결
        setContentView(R.layout.activity_info) // XML 레이아웃 파일 연결

        // 뷰에 대한 참조 설정
        val backButton: ImageView = findViewById(R.id.back_button)
        val titleText: TextView = findViewById(R.id.title_text)
        val mainImage: ImageView = findViewById(R.id.main_image)
        val placeName: TextView = findViewById(R.id.place_name)
        val location: TextView = findViewById(R.id.location)
        val description: TextView = findViewById(R.id.description)
        val addToItineraryButton: Button = findViewById(R.id.add_to_itinerary_button)

        // 뒤로 가기 버튼 클릭 이벤트
        backButton.setOnClickListener {
            // 뒤로 가기 동작 구현 (현재 액티비티를 종료하여 이전 화면으로 돌아감)
            onBackPressed()
        }

        // "여행 일정에 담기" 버튼 클릭 이벤트
        addToItineraryButton.setOnClickListener {
            // 버튼 클릭 시 동작을 정의할 수 있습니다.
            // 예: 일정에 장소를 추가하는 기능 구현
            addToItinerary()
        }

        // 필요에 따라 추가 동작을 설정할 수 있습니다.
        // 예: 서버에서 데이터를 가져와 뷰에 반영하는 코드
    }

    private fun addToItinerary() {
        // 일정에 장소를 추가하는 로직을 구현합니다.
        // 이 예제에서는 단순히 로그를 출력합니다.
        println("여행 일정에 장소를 추가했습니다.")
    }
}
