package com.example.oasis_con

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.*
import com.kakao.vectormap.RoadViewRequest.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)  // activity_map은 이 액티비티의 레이아웃 XML 파일이어야 합니다.

        mapView = findViewById(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()  // MapView 의 resume 호출
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()   // MapView 의 pause 호출
    }
}
