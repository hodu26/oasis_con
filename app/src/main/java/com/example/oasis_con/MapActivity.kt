package com.example.oasis_con

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.*
import com.kakao.vectormap.label.*
import com.kakao.vectormap.LatLng

data class Marker(
    var markerId: Long,
    var markerName: String? = null,
    var pos: LatLng
)

private var markers = ArrayList<Marker>()

class MapActivity : AppCompatActivity() {

    lateinit var mapView: MapView

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
                val lat1 = LatLng.from(33.452278,126.567803)
                val lat2 = LatLng.from(34.521247,127.315544)
                val lat3 = LatLng.from(33.891222,126.464988)

                val mark1 = Marker(1,"A", lat1)
                val mark2 = Marker(2,"B", lat2)
                val mark3 = Marker(3,"C", lat3)

                markers.add(mark1)
                markers.add(mark2)
                markers.add(mark3)


                for(data in markers){

                    val styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.location).setZoomLevel(0)))
                    val options = LabelOptions.from(data.pos).setStyles(styles)
                    val layer = kakaoMap.labelManager?.layer
                    val label = layer?.addLabel(options)
                }
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
