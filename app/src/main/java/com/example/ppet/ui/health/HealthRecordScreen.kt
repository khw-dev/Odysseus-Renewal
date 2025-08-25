package com.example.ppet.ui.health

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.data.model.HealthRecord
import com.example.ppet.data.model.HealthRecordType
import com.example.ppet.data.model.Pet
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordScreen(
    pet: Pet,
    healthRecords: List<HealthRecord>,
    onBack: () -> Unit,
    onAddRecord: () -> Unit,
    onRecordClick: (HealthRecord) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${pet.name}의 건강 기록",
                            fontFamily = NotoSansKR,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${pet.type} • ${pet.age}세",
                            fontFamily = NotoSansKR,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddRecord) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "기록 추가",
                            tint = OrangePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (healthRecords.isEmpty()) {
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "아직 건강 기록이 없습니다",
                    fontFamily = NotoSansKR,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "첫 번째 건강 기록을 추가해보세요",
                    fontFamily = NotoSansKR,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onAddRecord,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "기록 추가",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                items(healthRecords) { record ->
                    HealthRecordCard(
                        record = record,
                        onClick = { onRecordClick(record) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthRecordCard(
    record: HealthRecord,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getHealthRecordTypeColor(record.type).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = getHealthRecordTypeText(record.type),
                            fontFamily = NotoSansKR,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = getHealthRecordTypeColor(record.type),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    
                    Text(
                        text = record.title,
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    
                    if (!record.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = record.description,
                            fontFamily = NotoSansKR,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 2
                        )
                    }

                    
                    if (!record.veterinaryClinic.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📍 ${record.veterinaryClinic}",
                            fontFamily = NotoSansKR,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                
                Text(
                    text = dateFormatter.format(record.date),
                    fontFamily = NotoSansKR,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            
            record.nextAppointment?.let { nextDate ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OrangePrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "다음 예약: ${dateFormatter.format(nextDate)}",
                        fontFamily = NotoSansKR,
                        fontSize = 12.sp,
                        color = OrangePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun getHealthRecordTypeText(type: HealthRecordType): String {
    return when (type) {
        HealthRecordType.VACCINATION -> "예방접종"
        HealthRecordType.CHECKUP -> "건강검진"
        HealthRecordType.TREATMENT -> "치료"
        HealthRecordType.MEDICATION -> "투약"
        HealthRecordType.SURGERY -> "수술"
        HealthRecordType.DENTAL -> "치과"
        HealthRecordType.EMERGENCY -> "응급처치"
        HealthRecordType.OTHER -> "기타"
    }
}

private fun getHealthRecordTypeColor(type: HealthRecordType): Color {
    return when (type) {
        HealthRecordType.VACCINATION -> Color(0xFF4CAF50)
        HealthRecordType.CHECKUP -> Color(0xFF2196F3)
        HealthRecordType.TREATMENT -> OrangePrimary
        HealthRecordType.MEDICATION -> Color(0xFF9C27B0)
        HealthRecordType.SURGERY -> Color(0xFFF44336)
        HealthRecordType.DENTAL -> Color(0xFF00BCD4)
        HealthRecordType.EMERGENCY -> Color(0xFFFF5722)
        HealthRecordType.OTHER -> Color(0xFF607D8B)
    }
}
