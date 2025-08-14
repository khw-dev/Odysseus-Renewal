package com.example.ppet.ui.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun AddHealthRecordScreen(
    pet: Pet,
    onBack: () -> Unit,
    onSaveRecord: (HealthRecord) -> Unit
) {
    var selectedType by remember { mutableStateOf(HealthRecordType.CHECKUP) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var veterinaryClinic by remember { mutableStateOf("") }
    var hasNextAppointment by remember { mutableStateOf(false) }
    var nextAppointmentDate by remember { mutableStateOf(Date()) }
    var medication by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var showTypeDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showNextDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "건강 기록 추가",
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 반려동물 정보
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8F9FA)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${pet.name} (${pet.type}, ${pet.age}세)",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }

            // 기록 타입 선택
            ExposedDropdownMenuBox(
                expanded = showTypeDropdown,
                onExpandedChange = { showTypeDropdown = !showTypeDropdown }
            ) {
                OutlinedTextField(
                    value = getHealthRecordTypeText(selectedType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("기록 타입 *", fontFamily = NotoSansKR) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = showTypeDropdown
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showTypeDropdown,
                    onDismissRequest = { showTypeDropdown = false }
                ) {
                    HealthRecordType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(getHealthRecordTypeText(type), fontFamily = NotoSansKR) },
                            onClick = {
                                selectedType = type
                                showTypeDropdown = false
                            }
                        )
                    }
                }
            }

            // 제목
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목 *", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 날짜 선택
            OutlinedTextField(
                value = dateFormatter.format(selectedDate),
                onValueChange = {},
                readOnly = true,
                label = { Text("날짜 *", fontFamily = NotoSansKR) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "날짜 선택"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 병원명
            OutlinedTextField(
                value = veterinaryClinic,
                onValueChange = { veterinaryClinic = it },
                label = { Text("병원명", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 설명
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("설명", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 약물
            OutlinedTextField(
                value = medication,
                onValueChange = { medication = it },
                label = { Text("처방약/투약", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 비용
            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("비용", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("원", fontFamily = NotoSansKR) }
            )

            // 다음 예약 여부
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = hasNextAppointment,
                    onCheckedChange = { hasNextAppointment = it },
                    colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "다음 예약 있음",
                    fontFamily = NotoSansKR,
                    fontSize = 16.sp
                )
            }

            // 다음 예약 날짜
            if (hasNextAppointment) {
                OutlinedTextField(
                    value = dateFormatter.format(nextAppointmentDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("다음 예약 날짜", fontFamily = NotoSansKR) },
                    trailingIcon = {
                        IconButton(onClick = { showNextDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "날짜 선택"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 메모
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("메모", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 저장 버튼
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newRecord = HealthRecord(
                            id = UUID.randomUUID().toString(),
                            petId = pet.id,
                            type = selectedType,
                            title = title.trim(),
                            description = if (description.isNotBlank()) description.trim() else null,
                            date = selectedDate,
                            veterinaryClinic = if (veterinaryClinic.isNotBlank()) veterinaryClinic.trim() else null,
                            nextAppointment = if (hasNextAppointment) nextAppointmentDate else null,
                            medication = if (medication.isNotBlank()) medication.trim() else null,
                            cost = cost.toDoubleOrNull(),
                            notes = if (notes.isNotBlank()) notes.trim() else null
                        )
                        onSaveRecord(newRecord)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = title.isNotBlank()
            ) {
                Text(
                    text = "저장",
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }

    // 날짜 선택기들은 실제 구현에서는 DatePickerDialog를 사용해야 합니다
    // 여기서는 간단히 처리
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
