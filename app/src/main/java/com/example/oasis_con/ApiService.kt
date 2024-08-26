package com.example.oasis_con

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

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
        @Query("areaCode") areaCode: Int
    ): ApiResponse

    @GET("B551011/KorService1/detailInfo1")
    fun getDetailInfo(
        @Query("ServiceKey") serviceKey: String,
        @Query("contentId") contentId: String,
        @Query("contentTypeId") contentTypeId: String,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") type: String
    ): Call<DetailResponse>

    @GET("B551011/KorService1/searchKeyword1")
    fun searchKeyword(
        @Query("ServiceKey") serviceKey: String,
        @Query("keyword") keyword: String,
        @Query("MobileOS") mobileOS: String = "ETC",
        @Query("MobileApp") mobileApp: String = "AppTest",
        @Query("_type") type: String = "json",
        @Query("numOfRows") numOfRows: Int = 1
    ): Call<KeywordSearchResponse>

    @GET("B551011/KorService1/detailCommon1")
    fun getDetailCommon(
        @Query("ServiceKey") serviceKey: String,
        @Query("contentId") contentId: String,
        @Query("MobileOS") mobileOS: String = "ETC",
        @Query("MobileApp") mobileApp: String = "AppTest",
        @Query("_type") type: String = "json",
        @Query("defaultYN") defaultYN: String = "Y",
        @Query("firstImageYN") firstImageYN: String = "Y",
        @Query("areacodeYN") areacodeYN: String = "Y",
        @Query("catcodeYN") catcodeYN: String = "Y",
        @Query("addrinfoYN") addrinfoYN: String = "Y",
        @Query("mapinfoYN") mapinfoYN: String = "Y",
        @Query("overviewYN") overviewYN: String = "Y"
    ): Call<DetailCommonResponse>
}
data class KeywordSearchResponse(val response: KeywordSearchBody)
data class KeywordSearchBody(val body: KeywordSearchItems)
data class KeywordSearchItems(val items: List<KeywordSearchItem>)
data class KeywordSearchItem(val firstimage: String?)

data class DetailCommonResponse(val response: DetailCommonBody)
data class DetailCommonBody(val body: DetailCommonItems)
data class DetailCommonItems(val items: List<DetailCommonItem>)
data class DetailCommonItem(
    val firstimage: String?,
    val title: String?,
    val overview: String?
)