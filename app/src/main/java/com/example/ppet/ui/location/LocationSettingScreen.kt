package com.example.ppet.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary

data class LocationInfo(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSettingScreen(
    currentLocation: LocationInfo?,
    onBack: () -> Unit,
    onLocationSelected: (LocationInfo) -> Unit,
    onCurrentLocationRequest: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<LocationInfo>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "위치 설정",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 현��� 위치 정보
            currentLocation?.let { location ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = OrangePrimary.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "현재 설정된 위치",
                                fontFamily = NotoSansKR,
                                fontSize = 12.sp,
                                color = OrangePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = location.address,
                                fontFamily = NotoSansKR,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 현재 위치 사용 버튼
            Button(
                onClick = onCurrentLocationRequest,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "현재 위치 사용",
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            // 구분선
            HorizontalDivider(color = Color(0xFFF0F0F0))

            // 주소 검색
            Text(
                text = "주소로 검색",
                fontFamily = NotoSansKR,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.length >= 2) {
                        // 실제 구현에서는 여기서 주소 검색 API를 호출
                        searchResults = getSampleSearchResults(it)
                    } else {
                        searchResults = emptyList()
                    }
                },
                label = { Text("주소를 입��하세요", fontFamily = NotoSansKR) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 검색 결과
            if (searchResults.isNotEmpty()) {
                Text(
                    text = "검색 결과",
                    fontFamily = NotoSansKR,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { location ->
                        LocationSearchItem(
                            location = location,
                            onClick = { onLocationSelected(location) }
                        )
                    }
                }
            }

            // 자주 사용하는 위치
            if (searchQuery.isEmpty()) {
                Text(
                    text = "자주 사용하는 위치",
                    fontFamily = NotoSansKR,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                val frequentLocations = getSampleFrequentLocations()

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(frequentLocations) { location ->
                        LocationSearchItem(
                            location = location,
                            onClick = { onLocationSelected(location) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationSearchItem(
    location: LocationInfo,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = location.address,
                fontFamily = NotoSansKR,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun getSampleSearchResults(query: String): List<LocationInfo> {
    return listOf(
        LocationInfo("서울특별시 강남구 테헤란로 123", 37.5665, 126.9780),
        LocationInfo("서울특별시 강남구 역삼동 456", 37.5002, 127.0366),
        LocationInfo("서울특별시 서초구 서초대로 789", 37.4979, 127.0276)
    ).filter { it.address.contains(query) }
}

private fun getSampleFrequentLocations(): List<LocationInfo> {
    return listOf(
        LocationInfo("우리집", 37.5665, 126.9780),
        LocationInfo("회사", 37.5002, 127.0366),
        LocationInfo("단골 동물병원", 37.4979, 127.0276)
    )
}
