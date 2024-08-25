package com.example.oasis_con

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoMapSdk.init(this, "56b0287c3f520eb1a57b290d5e308229")  // "your_app_key"를 실제 API 키로 교체
    }
}
