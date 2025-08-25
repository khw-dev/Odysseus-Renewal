package com.example.ppet.ui.around

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.ui.around.manager.LocationManager
import com.example.ppet.ui.around.model.VetHospital
import com.example.ppet.ui.around.service.VetHospitalSearchService
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary
import com.example.ppet.utils.PhoneUtils
import kotlinx.coroutines.launch
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AroundScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val scope = rememberCoroutineScope()

    var hospitals by remember { mutableStateOf<List<VetHospital>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(locationManager.hasLocationPermission()) }
    var currentLocation by remember { mutableStateOf<android.location.Location?>(null) }
    var locationError by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            loadNearbyHospitals(locationManager, scope) { location, hospitalList, error ->
                currentLocation = location
                hospitals = hospitalList
                locationError = error
                isLoading = false
            }
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            isLoading = true
            loadNearbyHospitals(locationManager, scope) { location, hospitalList, error ->
                currentLocation = location
                hospitals = hospitalList
                locationError = error
                isLoading = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "주변 동물병원",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LocationCard(
            currentLocation = currentLocation,
            hasPermission = hasLocationPermission,
            locationError = locationError,
            onRequestPermission = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = OrangePrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            !hasLocationPermission || locationError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "주변 동물병원을 찾을 수 없습니다",
                            fontSize = 16.sp,
                            fontFamily = NotoSansKR,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        if (!hasLocationPermission) {
                            Text(
                                text = "위치 권한이 필요합니다",
                                fontSize = 14.sp,
                                fontFamily = NotoSansKR,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(hospitals) { hospital ->
                        HospitalCard(
                            hospital = hospital,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationCard(
    currentLocation: android.location.Location?,
    hasPermission: Boolean,
    locationError: Boolean,
    onRequestPermission: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F9FA)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "위치",
                tint = if (hasPermission && !locationError) OrangePrimary else Color.Gray,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "현재 위치",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = NotoSansKR
                )
                Text(
                    text = when {
                        !hasPermission -> "위치 권한이 필요합니다"
                        locationError -> "위치를 찾을 수 없습니다"
                        currentLocation != null -> "검색 완료"
                        else -> "위치 확인 중..."
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NotoSansKR,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!hasPermission) {
                Surface(
                    onClick = onRequestPermission,
                    shape = RoundedCornerShape(12.dp),
                    color = OrangePrimary
                ) {
                    Text(
                        text = "허용",
                        fontSize = 12.sp,
                        fontFamily = NotoSansKR,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HospitalCard(
    hospital: VetHospital,
    context: android.content.Context
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 상세 정보 화면으로 이동 */ },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hospital.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NotoSansKR,
                    modifier = Modifier.weight(1f)
                )

//                Surface(
//                    shape = RoundedCornerShape(8.dp),
//                    color = if (hospital.isOpen) Color(0xFF4CAF50) else Color(0xFFFF5722)
//                ) {
//                    Text(
//                        text = if (hospital.isOpen) "영업중" else "영업종료",
//                        color = Color.White,
//                        fontSize = 10.sp,
//                        fontFamily = NotoSansKR,
//                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
//                    )
//                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "주소",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${hospital.address} • ${hospital.distance}km",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontFamily = NotoSansKR,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "평점",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = hospital.rating.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSansKR
                    )
                }

                Text(
                    text = hospital.openHours,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = NotoSansKR
                )
            }

            if (hospital.specialties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    hospital.specialties.take(3).forEach { specialty ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OrangePrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = specialty,
                                fontSize = 11.sp,
                                color = OrangePrimary,
                                fontFamily = NotoSansKR,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

//            Surface(
//                onClick = {
//                    PhoneUtils.makePhoneCall(context, hospital.phoneNumber)
//                },
//                modifier = Modifier.fillMaxWidth(),
//                color = OrangePrimary,
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Row(
//                    modifier = Modifier.padding(vertical = 12.dp),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Phone,
//                        contentDescription = "전화",
//                        modifier = Modifier.size(16.dp),
//                        tint = Color.White
//                    )
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text(
//                        text = "전화하기",
//                        fontFamily = NotoSansKR,
//                        fontWeight = FontWeight.Medium,
//                        fontSize = 14.sp,
//                        color = Color.White
//                    )
//                }
//            }
        }
    }
}

private fun loadNearbyHospitals(
    locationManager: LocationManager,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (android.location.Location?, List<VetHospital>, Boolean) -> Unit
) {
    scope.launch {
        try {
            Log.d("AroundScreen", "=== 병원 검색 요청 시작 ===")

            val location = locationManager.getCurrentLocation()

            if (location != null) {
                Log.d("AroundScreen", "위치 획득 성공: ${location.latitude}, ${location.longitude}")

                // TODO: 실제 위치 기반 검색
                val searchService = VetHospitalSearchService(locationManager.context)
                val hospitals = searchService.searchNearbyVetHospitals(location)

                Log.d("AroundScreen", "검색 완료 - 병원 수: ${hospitals.size}")

                if (hospitals.isNotEmpty()) {
                    hospitals.forEach { hospital ->
                        Log.d("AroundScreen", "- ${hospital.name} (${hospital.distance}km)")
                    }
                }

                onResult(location, hospitals, false)
            } else {
                Log.w("AroundScreen", "위치 획득 실패")
                onResult(null, emptyList(), true)
            }
        } catch (e: Exception) {
            Log.e("AroundScreen", "병원 검색 중 오류 발생", e)
            onResult(null, emptyList(), true)
        }
    }
}
