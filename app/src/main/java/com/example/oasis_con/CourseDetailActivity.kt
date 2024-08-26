package com.example.oasis_con

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.oasis_con.ApiService
import retrofit2.http.GET
import retrofit2.http.Query
class CourseDetailActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        recyclerView = findViewById(R.id.recyclerViewDetail)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        adapter = CourseDetailAdapter(emptyList(), apiService, "YOUR_SERVICE_KEY_HERE")
        recyclerView.adapter = adapter

        val contentId = intent.getStringExtra("contentId") ?: return
        fetchCourseDetails(contentId)
    }

    private fun fetchCourseDetails(contentId: String) {
        Log.d("API_CALL", "Fetching details for contentId: $contentId")
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getDetailInfo(
            serviceKey = "fQkSfrlIO/O+qEyS+oclHBllyMuB8aBKpu2f1LVLAo2ffOoaSQueJrWVr7eqpRCRMSoqHaOFETU+VBXhtxOaFQ==",
            contentId = contentId,
            contentTypeId = "25",
            mobileOS = "ETC",
            mobileApp = "AppTest",
            type = "json"  // JSON 응답 요청
        )

        call.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                Log.d("API_RESPONSE", "Response code: ${response.code()}")
                Log.d("API_RESPONSE", "Response body: ${response.body()}")
                if (response.isSuccessful) {
                    val detailItems = response.body()?.response?.body?.items?.item ?: emptyList()
                    if (detailItems.isEmpty()) {
                        Log.d("API_RESPONSE", "No items found in the response")
                        Toast.makeText(this@CourseDetailActivity, "상세 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("API_RESPONSE", "Items found: ${detailItems.size}")
                        adapter.updateItems(detailItems)
                    }
                } else {
                    Log.e("API_ERROR", "Unsuccessful response: ${response.errorBody()?.string()}")
                    Toast.makeText(this@CourseDetailActivity, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                Log.e("API_ERROR", "Network error: ${t.message}", t)
                Toast.makeText(this@CourseDetailActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

data class DetailResponse(val response: DetailResponseBody)
data class DetailResponseBody(val body: DetailResponseBodyContent)
data class DetailResponseBodyContent(val items: DetailItems)
data class DetailItems(val item: List<CourseDetail>)
data class CourseDetail(
    val subcontentid: String,
    val subname: String,
    val subdetailoverview: String,
    val subdetailimg: String? // 이미지 URL을 저장할 필드 추가
)