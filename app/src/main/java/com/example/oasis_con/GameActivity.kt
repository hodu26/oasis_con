package com.example.oasis_con

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.LatLngBounds
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import kotlin.math.min
import kotlin.random.Random
import android.content.Intent

class GameActivity : AppCompatActivity() {

    // Kakao Map
    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap

    // Game
    private lateinit var arrow: ImageView
    private lateinit var mapFrame: View // FrameLayout을 사용하여 히트 범위를 설정
    private var touchStartTime: Long = 0

    // ObjectAnimator들
    private lateinit var scaleAnimator: ObjectAnimator
    private lateinit var translationXAnimator: ObjectAnimator
    private lateinit var translationYAnimator: ObjectAnimator

    // 호남권만 나오도록 지도 범위 설정
    private val southwest = LatLng.from(34.0, 126.0) // 남서쪽 좌표 (전라남도 서쪽)
    private val northeast = LatLng.from(36.5, 128.0) // 북동쪽 좌표 (전라북도 동쪽)
    private val bounds = LatLngBounds(southwest, northeast)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Kakao Map 설정
        mapFrame = findViewById(R.id.map_frame) // FrameLayout 참조
        mapView = findViewById(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@GameActivity.kakaoMap = kakaoMap // KakaoMap 객체 초기화

                // 지도 카메라를 범위에 맞춰 이동
                kakaoMap.moveCamera(com.kakao.vectormap.camera.CameraUpdateFactory.fitMapPoints(bounds))

                // 모든 제스처 비활성화
                disableMapGestures()

                // MapView에 포커스를 주지 않고 터치 이벤트 비활성화
                mapView.setOnTouchListener { _, _ -> true }
                mapView.isFocusable = false
                mapView.isFocusableInTouchMode = false

                // 지도 움직임 애니메이션 시작
                startMapAnimation()
                startTargetAnimation()
            }
        })

        // Game
        arrow = findViewById(R.id.arrow)

        // 터치 이벤트 처리
        arrow.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0f
            private var initialY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 처음 터치한 시간과 위치를 기록
                        touchStartTime = System.currentTimeMillis()
                        initialX = v.x
                        initialY = v.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // 화살을 드래그할 때 위치 업데이트
                        v.x = event.rawX - v.width / 2
                        v.y = event.rawY - v.height / 2
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // 터치를 떼면 화살을 발사
                        val touchDuration = System.currentTimeMillis() - touchStartTime
                        shootArrow(touchDuration)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun disableMapGestures() {
        kakaoMap.setGestureEnable(GestureType.getEnum(1), false) // 스크롤 비활성화
        kakaoMap.setGestureEnable(GestureType.getEnum(2), false) // 줌 비활성화
        kakaoMap.setGestureEnable(GestureType.getEnum(3), false) // 회전 비활성화
        kakaoMap.setGestureEnable(GestureType.getEnum(4), false) // 기울기 비활성화
    }

    private fun startTargetAnimation() {
        // 타겟의 크기 애니메이션 (커졌다 작아졌다)
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f, 1f)
        scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(mapView, scaleX, scaleY)
        scaleAnimator.duration = 1000 // 1초 동안 애니메이션
        scaleAnimator.repeatCount = ObjectAnimator.INFINITE // 무한 반복
        scaleAnimator.start()
    }

    private fun startMapAnimation() {
        // 지도 뷰의 부모 레이아웃 크기를 가져옴
        val parentWidth = (mapView.parent as View).width.toFloat()
        val parentHeight = (mapView.parent as View).height.toFloat()

        // 지도 뷰가 랜덤하게 움직이도록 설정
        val randomX = (Random.nextFloat() - 0.5f) * 2 * (parentWidth - mapView.width) // -1 ~ 1 사이의 값으로 계산
        val randomY = (Random.nextFloat() - 0.5f) * 2 * (parentHeight - mapView.height) // -1 ~ 1 사이의 값으로 계산

        translationXAnimator = ObjectAnimator.ofFloat(
            mapView, "translationX", 0f, randomX.coerceIn(-parentWidth, parentWidth)
        )
        translationYAnimator = ObjectAnimator.ofFloat(
            mapView, "translationY", 0f, randomY.coerceIn(-parentHeight, parentHeight)
        )

        translationXAnimator.duration = 3000 // 3초 동안 이동
        translationYAnimator.duration = 3000 // 3초 동안 이동

        translationXAnimator.repeatCount = ObjectAnimator.INFINITE // 무한 반복
        translationYAnimator.repeatCount = ObjectAnimator.INFINITE // 무한 반복

        translationXAnimator.repeatMode = ObjectAnimator.REVERSE // 이동 후 반대 방향으로 이동
        translationYAnimator.repeatMode = ObjectAnimator.REVERSE // 이동 후 반대 방향으로 이동

        translationXAnimator.start()
        translationYAnimator.start()
    }


    private fun shootArrow(touchDuration: Long) {
        // 터치 시간을 기반으로 화살이 날아갈 거리 계산
        val screenHeight = (arrow.parent as View).height
        val durationFactor = min(touchDuration / 1000f, 3f) // 터치 시간에 따른 거리 비율 (최대 3초)
        val distanceToFly = screenHeight * durationFactor

        // 화살이 위로 날아가는 애니메이션
        val arrowFly = ObjectAnimator.ofFloat(arrow, "y", arrow.y, arrow.y - distanceToFly)
        arrowFly.duration = 1000 // 1초 동안 날아가게 설정
        arrowFly.interpolator = DecelerateInterpolator()

        // 크기 조절 애니메이션 (원근감 효과)
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            arrow,
            android.animation.PropertyValuesHolder.ofFloat("scaleX", 0.5f),
            android.animation.PropertyValuesHolder.ofFloat("scaleY", 0.5f)
        )
        scaleDown.duration = 500 // 첫 0.5초 동안 작아짐
        scaleDown.startDelay = 500 // scaleDown 이후에 실행되도록 설정

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            arrow,
            android.animation.PropertyValuesHolder.ofFloat("scaleX", 1.5f),
            android.animation.PropertyValuesHolder.ofFloat("scaleY", 1.5f)
        )
        scaleUp.duration = 500 // 나머지 0.5초 동안 다시 커짐

        // 애니메이션이 끝났을 때, 충돌 여부 확인
        arrowFly.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                checkIfHit()
            }
        })

        arrowFly.start()
        scaleDown.start()
        scaleUp.start()
    }

    private fun checkIfHit() {
        val arrowLocation = IntArray(2)
        val mapFrameLocation = IntArray(2)

        arrow.getLocationOnScreen(arrowLocation)
        mapFrame.getLocationOnScreen(mapFrameLocation)

        if (arrowLocation[0] < mapFrameLocation[0] + mapFrame.width &&
            arrowLocation[0] + arrow.width > mapFrameLocation[0] &&
            arrowLocation[1] < mapFrameLocation[1] + mapFrame.height &&
            arrowLocation[1] + arrow.height > mapFrameLocation[1]) {

            stopMapAnimation()

            if (this::kakaoMap.isInitialized) {
                val latLng: LatLng? = kakaoMap.fromScreenPoint(arrowLocation[0], arrowLocation[1])

                // ItemActivity로 전환
                val intent = Intent(this, ItemActivity::class.java)
                intent.putExtra("latitude", latLng?.latitude)
                intent.putExtra("longitude", latLng?.longitude)
                startActivity(intent)

                // 현재 액티비티 종료 (선택사항)
                // finish()
            } else {
                Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_LONG).show()
            }

            Toast.makeText(this, "Hit!", Toast.LENGTH_SHORT).show()
        } else {
            resetArrow()
            Toast.makeText(this, "Miss!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopMapAnimation() {
        // 타겟 애니메이션을 멈추는 로직
        if (this::scaleAnimator.isInitialized) {
            scaleAnimator.cancel()
        }
        if (this::translationXAnimator.isInitialized) {
            translationXAnimator.cancel()
        }
        if (this::translationYAnimator.isInitialized) {
            translationYAnimator.cancel()
        }
    }

    private fun resetArrow() {
        // 화살의 위치를 초기 위치로 리셋
        arrow.x = (arrow.parent as View).width / 2f - arrow.width / 2f
        arrow.y = (arrow.parent as View).height - arrow.height.toFloat()
    }
}
