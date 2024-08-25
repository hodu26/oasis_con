package com.example.oasis_con

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class ItemActivity : AppCompatActivity() {

    private lateinit var btnFetchData: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    private var currentPage = 1
    private val maxPage = 6
    private val itemsList = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)

        btnFetchData = findViewById(R.id.btnFetchData)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = ItemAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnFetchData.setOnClickListener {
            currentPage = 1
            itemsList.clear()
            fetchData()
        }
    }

    private fun fetchData() {
        lifecycleScope.launch {
            try {
                while (currentPage <= maxPage) {
                    val response = withContext(Dispatchers.IO) {
                        apiService.getAreaBasedList(
                            serviceKey = "fQkSfrlIO/O+qEyS+oclHBllyMuB8aBKpu2f1LVLAo2ffOoaSQueJrWVr7eqpRCRMSoqHaOFETU+VBXhtxOaFQ==",
                            numOfRows = 12,
                            pageNo = currentPage,
                            mobileOS = "ETC",
                            mobileApp = "AppTest",
                            type = "json",
                            listYN = "Y",
                            arrange = "A",
                            contentTypeId = 25,
                            areaCode = "37",
                            sigunguCode = "",
                            cat1 = "",
                            cat2 = "",
                            cat3 = ""
                        )
                    }
                    val newItems = response.response.body.items.item
                    if (newItems.isNotEmpty()) {
                        itemsList.addAll(newItems)
                        adapter.updateItems(itemsList)
                        currentPage++
                    } else {
                        break  // 더 이상 데이터가 없으면 루프 종료
                    }
                }
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
    @GET("B551011/KorService1/areaBasedList1")
    suspend fun getAreaBasedList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") type: String,
        @Query("listYN") listYN: String,
        @Query("arrange") arrange: String,
        @Query("contentTypeId") contentTypeId: Int,
        @Query("areaCode") areaCode: String,
        @Query("sigunguCode") sigunguCode: String,
        @Query("cat1") cat1: String,
        @Query("cat2") cat2: String,
        @Query("cat3") cat3: String
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