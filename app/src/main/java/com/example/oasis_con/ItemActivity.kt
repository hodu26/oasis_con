package com.example.oasis_con

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class ItemActivity : AppCompatActivity() {

    private lateinit var btnFetchData: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)

        btnFetchData = findViewById(R.id.btnFetchData)
        recyclerView = findViewById(R.id.recyclerView)
        etLatitude = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)

        adapter = ItemAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnFetchData.setOnClickListener {
            val latitude = etLatitude.text.toString().toDoubleOrNull()
            val longitude = etLongitude.text.toString().toDoubleOrNull()
            if (latitude != null && longitude != null) {
                fetchData(latitude, longitude)
            } else {
                Toast.makeText(this, "유효한 좌표를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchData(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getLocationBasedList(
                        serviceKey = "fQkSfrlIO/O+qEyS+oclHBllyMuB8aBKpu2f1LVLAo2ffOoaSQueJrWVr7eqpRCRMSoqHaOFETU+VBXhtxOaFQ==",
                        numOfRows = 20,
                        pageNo = 1,
                        mobileOS = "ETC",
                        mobileApp = "AppTest",
                        type = "json",
                        listYN = "Y",
                        arrange = "A",
                        mapX = longitude,
                        mapY = latitude,
                        radius = 20000,  // 20km 반경
                        contentTypeId = 12  // 관광지 타입
                    )
                }
                adapter.updateItems(response.response.body.items.item)
                Toast.makeText(this@ItemActivity, "데이터 조회 완료!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ItemActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Error: ${e.message}", e)
            }
        }
    }

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    @GET("B551011/KorService1/locationBasedList1")
    suspend fun getLocationBasedList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") type: String,
        @Query("listYN") listYN: String,
        @Query("arrange") arrange: String,
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
        @Query("contentTypeId") contentTypeId: Int
    ): ApiResponse
}

data class ApiResponse(val response: Response)
data class Response(val header: Header, val body: Body)
data class Header(val resultCode: String, val resultMsg: String)
data class Body(val items: Items, val numOfRows: Int, val pageNo: Int, val totalCount: Int)
data class Items(val item: List<Item>)
data class Item(
    val addr1: String?,
    val addr2: String?,
    val areacode: String?,
    val booktour: String?,
    val cat1: String?,
    val cat2: String?,
    val cat3: String?,
    val contentid: String?,
    val contenttypeid: String?,
    val createdtime: String?,
    val firstimage: String?,
    val firstimage2: String?,
    val cpyrhtDivCd: String?,
    val mapx: String?,
    val mapy: String?,
    val mlevel: String?,
    val modifiedtime: String?,
    val sigungucode: String?,
    val tel: String?,
    val title: String?,
    val zipcode: String?
)