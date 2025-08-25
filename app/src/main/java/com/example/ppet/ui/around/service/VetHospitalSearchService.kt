package com.example.ppet.ui.around.service

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.ppet.ui.around.model.VetHospital
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class VetHospitalSearchService(private val context: Context) {

    
    private val kakaoApiKey = "a1e01dc0cb86278d9dee1317b8526447"

    companion object {
        private const val TAG = "VetHospitalSearch"
    }

    suspend fun searchNearbyVetHospitals(location: Location): List<VetHospital> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== 동물병원 검색 시작 ===")
            Log.d(TAG, "위치: 위도=${location.latitude}, 경도=${location.longitude}")

            
            delay(1500) 

            val hospitals = generateDummyHospitals(location)
            Log.d(TAG, "더미 데이터 생성 완료 - 병원 수: ${hospitals.size}")

            hospitals.forEach { hospital ->
                Log.d(TAG, "병원: ${hospital.name} (${hospital.distance}km)")
            }

            Log.d(TAG, "=== 검색 성공 ===")
            hospitals

        } catch (e: Exception) {
            Log.e(TAG, "=== 검색 실패 ===")
            Log.e(TAG, "오류 타입: ${e.javaClass.simpleName}")
            Log.e(TAG, "오류 메시지: ${e.message}")
            return@withContext emptyList()
        }
    }

    private fun generateDummyHospitals(userLocation: Location): List<VetHospital> {
        val dummyHospitals = listOf(
            VetHospital(
                id = "dummy1",
                name = "펫케어 동물의료센터",
                address = "서울시 강남구 신사동 123-45",
                phoneNumber = "02-3456-7890",
                distance = 0.3,
                rating = 4.9f,
                isOpen = true,
                openHours = "24시간 운영",
                specialties = listOf("응급진료", "수술"),
                latitude = userLocation.latitude + 0.001,
                longitude = userLocation.longitude + 0.001
            ),
            VetHospital(
                id = "dummy2",
                name = "미래동물병원",
                address = "서울시 서초구 반포동 567-89",
                phoneNumber = "02-5678-9012",
                distance = 0.7,
                rating = 4.6f,
                isOpen = true,
                openHours = "09:00 - 21:00",
                specialties = listOf("내과", "치과"),
                latitude = userLocation.latitude - 0.002,
                longitude = userLocation.longitude + 0.003
            ),
            VetHospital(
                id = "dummy3",
                name = "별빛동물메디컬센터",
                address = "서울시 마포구 홍대입구 234-56",
                phoneNumber = "02-7890-1234",
                distance = 1.2,
                rating = 4.8f,
                isOpen = false,
                openHours = "09:00 - 18:00",
                specialties = listOf("피부과", "검진"),
                latitude = userLocation.latitude + 0.004,
                longitude = userLocation.longitude - 0.002
            ),
            VetHospital(
                id = "dummy4",
                name = "그린힐동물병원",
                address = "서울시 영등포구 여의도동 345-67",
                phoneNumber = "02-2345-6789",
                distance = 1.8,
                rating = 4.4f,
                isOpen = true,
                openHours = "08:00 - 20:00",
                specialties = listOf("외과", "재활"),
                latitude = userLocation.latitude - 0.003,
                longitude = userLocation.longitude - 0.004
            ),
            VetHospital(
                id = "dummy5",
                name = "도담동물클리닉",
                address = "서울시 송파구 잠실동 456-78",
                phoneNumber = "02-8901-2345",
                distance = 2.1,
                rating = 4.7f,
                isOpen = true,
                openHours = "10:00 - 22:00",
                specialties = listOf("예방접종", "건강검진"),
                latitude = userLocation.latitude + 0.005,
                longitude = userLocation.longitude + 0.003
            ),
            VetHospital(
                id = "dummy6",
                name = "해피펫동물병원",
                address = "서울시 관악구 신림동 678-90",
                phoneNumber = "02-3456-7891",
                distance = 2.5,
                rating = 4.3f,
                isOpen = false,
                openHours = "09:00 - 19:00",
                specialties = listOf("심장과", "신경과"),
                latitude = userLocation.latitude - 0.006,
                longitude = userLocation.longitude + 0.002
            ),
            VetHospital(
                id = "dummy7",
                name = "스마일동물의원",
                address = "서울시 중구 명동 789-01",
                phoneNumber = "02-4567-8901",
                distance = 2.9,
                rating = 4.5f,
                isOpen = true,
                openHours = "07:00 - 23:00",
                specialties = listOf("안과", "이비인후과"),
                latitude = userLocation.latitude + 0.003,
                longitude = userLocation.longitude - 0.005
            ),
            VetHospital(
                id = "dummy8",
                name = "라이프동물종합병원",
                address = "서울시 종로구 종로1가 890-12",
                phoneNumber = "02-5678-9012",
                distance = 3.4,
                rating = 4.9f,
                isOpen = true,
                openHours = "24시간 운영",
                specialties = listOf("종합진료", "중환자실"),
                latitude = userLocation.latitude - 0.007,
                longitude = userLocation.longitude - 0.003
            )
        )

        return dummyHospitals.sortedBy { it.distance }
    }
}
