package com.example.oasis_con

import ItemAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import com.google.gson.GsonBuilder
import com.example.oasis_con.ApiService


class ItemActivity : AppCompatActivity() {
    private lateinit var btnFetchData: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var tvCoordinates: TextView

    private val apiService: ApiService by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)

        btnFetchData = findViewById(R.id.btnFetchData)
        recyclerView = findViewById(R.id.recyclerView)
        tvCoordinates = findViewById(R.id.tvCoordinates)

        adapter = ItemAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        tvCoordinates.text = "위도: $latitude \n경도: $longitude"

        fetchData(latitude, longitude)

        btnFetchData.setOnClickListener {
            fetchData(latitude, longitude)
        }
    }

    private fun fetchData(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            try {
                val allItems = mutableListOf<Item>()

                // 전라북도 데이터 가져오기
                fetchAreaData(allItems, 37, 6)

                // 전라남도 데이터 가져오기
                fetchAreaData(allItems, 38, 9)

                // 거리 계산 및 필터링
                val maxDistance = 25.0 // 최대 거리 (km)
                val filteredAndSortedItems = allItems
                    .map { item ->
                        val itemLat = item.mapy?.toDoubleOrNull() ?: 0.0
                        val itemLon = item.mapx?.toDoubleOrNull() ?: 0.0
                        val distance = calculateDistance(latitude, longitude, itemLat, itemLon)
                        item.apply { this.distance = distance }
                    }
                    .filter { it.distance <= maxDistance }
                    .sortedBy { it.distance }

                adapter.updateItems(filteredAndSortedItems)
                Toast.makeText(this@ItemActivity, "데이터 조회 완료!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ItemActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Error: ${e.message}", e)
            }
        }
    }

    private suspend fun fetchAreaData(items: MutableList<Item>, areaCode: Int, maxPages: Int) {
        for (page in 1..maxPages) {
            val response = withContext(Dispatchers.IO) {
                apiService.getAreaBasedList(
                    serviceKey = "fQkSfrlIO/O+qEyS+oclHBllyMuB8aBKpu2f1LVLAo2ffOoaSQueJrWVr7eqpRCRMSoqHaOFETU+VBXhtxOaFQ==",
                    numOfRows = 12,
                    pageNo = page,
                    mobileOS = "ETC",
                    mobileApp = "AppTest",
                    type = "json",
                    listYN = "Y",
                    arrange = "A",
                    contentTypeId = 25,
                    areaCode = areaCode
                )
            }
            items.addAll(response.response.body.items.item)
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // 지구의 반경 (km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
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
    val zipcode: String?,
    var distance: Double = 0.0 // 거리 정보를 저장할 새 필드
)