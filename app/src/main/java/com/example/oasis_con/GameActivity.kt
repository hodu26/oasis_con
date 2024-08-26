package com.example.oasis_con

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnCameraMoveEndListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.LatLngBounds
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import kotlin.math.min

class GameActivity : AppCompatActivity() {

    // kakao map
    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap

    // game
    private lateinit var arrow: ImageView
    private var touchStartTime: Long = 0

    // ObjectAnimator들을 멤버 변수로 선언하여 나중에 접근할 수 있도록 함
    private lateinit var scaleAnimator: ObjectAnimator
    private lateinit var translationXAnimator: ObjectAnimator

    // 호남권만 나오도록 지도 범위 설정
    val southwest = LatLng.from(34.0, 126.0) // 남서쪽 좌표 (전라남도 서쪽)
    val northeast = LatLng.from(36.5, 128.0) // 북동쪽 좌표 (전라북도 동쪽)
    val bounds = LatLngBounds(southwest, northeast)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

//        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

        // kakao map
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
                this@GameActivity.kakaoMap = kakaoMap // KakaoMap 객체 초기화

                // 지도 카메라를 범위에 맞춰 이동
                kakaoMap.moveCamera(com.kakao.vectormap.camera.CameraUpdateFactory.fitMapPoints(bounds))

                // 모든 제스처 비활성화
                kakaoMap.setGestureEnable(GestureType.getEnum(1), false) // 스크롤 비활성화
                kakaoMap.setGestureEnable(GestureType.getEnum(2), false) // 스크롤 비활성화
                kakaoMap.setGestureEnable(GestureType.getEnum(3), false) // 스크롤 비활성화
                kakaoMap.setGestureEnable(GestureType.getEnum(4), false) // 스크롤 비활성화

                // MapView에 포커스를 주지 않고 터치 이벤트 비활성화
                mapView.setOnTouchListener { _, _ -> true }
                mapView.isFocusable = false
                mapView.isFocusableInTouchMode = false
            }
        })

        // game
        arrow = findViewById(R.id.arrow)

        // 타겟의 크기 및 위치 애니메이션 시작
        startTargetAnimation()

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

//    private fun startTargetRotation() {
//        // 타겟이 360도 회전하는 애니메이션
//        val rotation = ObjectAnimator.ofFloat(target, "rotation", 0f, 360f)
//        rotation.duration = 2000 // 2초 동안 한 바퀴 회전
//        rotation.repeatCount = ObjectAnimator.INFINITE // 무한 반복
//        rotation.start()
//    }

    private fun startTargetAnimation() {
        // 타겟의 크기 애니메이션 (커졌다 작아졌다)
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f, 1f)
        scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(mapView, scaleX, scaleY)
        scaleAnimator.duration = 1000 // 1초 동안 애니메이션
        scaleAnimator.repeatCount = ObjectAnimator.INFINITE // 무한 반복
        scaleAnimator.start()

        // 타겟의 좌우 이동 애니메이션
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        translationXAnimator = ObjectAnimator.ofFloat(mapView, "translationX", -screenWidth / 4, screenWidth / 4)
        translationXAnimator.duration = 2000 // 2초 동안 좌우 이동
        translationXAnimator.repeatCount = ObjectAnimator.INFINITE // 무한 반복
        translationXAnimator.repeatMode = ObjectAnimator.REVERSE // 이동 후 반대 방향으로 이동
        translationXAnimator.start()
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
        // 간단히 충돌 여부를 확인하는 로직
        val arrowLocation = IntArray(2)
        val targetLocation = IntArray(2)

        arrow.getLocationOnScreen(arrowLocation)

        // 화살이 화면에서 떨어진 좌표를 받아옴
        val arrowX = arrowLocation[0]
        val arrowY = arrowLocation[1]

        // kakaoMap 초기화 확인
        if (this::kakaoMap.isInitialized) {
            // 화살이 떨어진 화면 좌표를 지도 좌표로 변환
            val latLng: LatLng? = kakaoMap.fromScreenPoint(arrowX, arrowY)

            // 변환된 지도 좌표를 로그로 출력하거나, 지도에 마커를 추가하는 등의 작업을 할 수 있음
            Toast.makeText(this, "Arrow hit at: ${latLng?.latitude}, ${latLng?.longitude}", Toast.LENGTH_LONG).show()
        } else {
            // 초기화되지 않은 경우, 예외 처리 (예: 로그 출력 또는 기본 동작)
            Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_LONG).show()
        }

        mapView.getLocationOnScreen(targetLocation)

        // 화살과 표적의 위치를 비교하여 충돌을 확인
        if (arrowLocation[0] < targetLocation[0] + mapView.width &&
            arrowLocation[0] + arrow.width > targetLocation[0] &&
            arrowLocation[1] < targetLocation[1] + mapView.height &&
            arrowLocation[1] + arrow.height > targetLocation[1]) {
            // 충돌한 경우
            stopTargetAnimation() // 충돌하면 타겟 애니메이션을 멈춤
//            getArrowLocation()
            Toast.makeText(this, "Hit!", Toast.LENGTH_SHORT).show()
        } else {
            // 충돌하지 않은 경우
            // 화살을 원래 위치로 리셋
            resetArrow()
            Toast.makeText(this, "Miss!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getArrowLocation() {
        val styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.arrow)))
        val options = LabelOptions.from(LatLng.from(37.394660, 127.111182)).setStyles(styles)
        val layer = kakaoMap.labelManager?.layer
        val label = layer?.addLabel(options)
    }


    private fun stopTargetAnimation() {
        // 타겟 애니메이션을 멈추는 로직
        if (this::scaleAnimator.isInitialized) {
            scaleAnimator.cancel()
        }
        if (this::translationXAnimator.isInitialized) {
            translationXAnimator.cancel()
        }
    }

    private fun resetArrow() {
        // 화살의 위치를 초기 위치로 리셋
        arrow.x = (arrow.parent as View).width / 2f - arrow.width / 2f
        arrow.y = (arrow.parent as View).height - arrow.height.toFloat()
    }
}
