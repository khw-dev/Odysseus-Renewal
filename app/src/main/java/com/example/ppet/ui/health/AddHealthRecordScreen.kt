package com.example.ppet.ui.health

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.ppet.ui.components.*
import com.example.ppet.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordScreen(
    pet: Pet,
    onBack: () -> Unit,
    onSave: (HealthRecord) -> Unit
) {
    var selectedType by remember { mutableStateOf(HealthRecordType.CHECKUP) }
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var weight by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var symptom by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var medication by remember { mutableStateOf("") }
    var veterinarian by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN)
    val isFormValid = title.isNotBlank()

    val healthRecordTypes = HealthRecordType.entries.map { type ->
        when (type) {
            HealthRecordType.CHECKUP -> "건강검진"
            HealthRecordType.VACCINATION -> "예방접종"
            HealthRecordType.TREATMENT -> "치료"
            HealthRecordType.MEDICATION -> "투약"
            HealthRecordType.SURGERY -> "수술"
            HealthRecordType.DENTAL -> "치과"
            HealthRecordType.EMERGENCY -> "응급처치"
            HealthRecordType.OTHER -> "기타"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${pet.name}의 건강 기록",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            PPetDropdownField(
                value = healthRecordTypes[selectedType.ordinal],
                onValueChange = { selectedValue ->
                    val index = healthRecordTypes.indexOf(selectedValue)
                    if (index != -1) {
                        selectedType = HealthRecordType.entries[index]
                    }
                },
                label = "기록 타입",
                options = healthRecordTypes,
                placeholder = "기록 타입을 선택하세요",
                leadingIcon = Icons.Default.Category
            )

            
            PPetTextField(
                value = title,
                onValueChange = { title = it },
                label = "제목",
                placeholder = "기록 제목을 입력하세요",
                leadingIcon = Icons.Default.Title,
                isError = title.isBlank() && title.isNotEmpty(),
                errorMessage = "제목을 입력해주세요"
            )

            
            PPetTextField(
                value = dateFormatter.format(selectedDate),
                onValueChange = { },
                label = "날짜",
                placeholder = "날짜를 선택하세요",
                leadingIcon = Icons.Default.DateRange,
                readOnly = true,
                modifier = Modifier.clickable { showDatePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "날짜 선택",
                            tint = OrangePrimary
                        )
                    }
                }
            )

            
            PPetTextField(
                value = weight,
                onValueChange = { weight = it },
                label = "체중",
                placeholder = "체중을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.Scale,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Text(
                        text = "kg",
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            
            PPetTextField(
                value = temperature,
                onValueChange = { temperature = it },
                label = "체온",
                placeholder = "체온을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.Thermostat,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Text(
                        text = "°C",
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            
            PPetTextField(
                value = symptom,
                onValueChange = { symptom = it },
                label = "증상",
                placeholder = "증상을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.HealthAndSafety
            )

            
            PPetTextField(
                value = treatment,
                onValueChange = { treatment = it },
                label = "치료 내용",
                placeholder = "치료 내용을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.MedicalServices
            )

            
            PPetTextField(
                value = medication,
                onValueChange = { medication = it },
                label = "투약 내용",
                placeholder = "투약 내용을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.Medication
            )

            
            PPetTextField(
                value = veterinarian,
                onValueChange = { veterinarian = it },
                label = "담당 수의사",
                placeholder = "담당 수의사명을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.Person
            )

            
            PPetTextField(
                value = hospital,
                onValueChange = { hospital = it },
                label = "병원",
                placeholder = "병원명을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.LocalHospital
            )

            
            PPetTextFieldMultiline(
                value = notes,
                onValueChange = { notes = it },
                label = "메모",
                placeholder = "추가 메모를 입력하세요 (선택사항)",
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(20.dp))

            
            Button(
                onClick = {
                    if (isFormValid) {
                        
                        val descriptionParts = mutableListOf<String>()

                        if (weight.isNotBlank()) descriptionParts.add("체중: ${weight}kg")
                        if (temperature.isNotBlank()) descriptionParts.add("체온: ${temperature}°C")
                        if (symptom.isNotBlank()) descriptionParts.add("증상: $symptom")
                        if (treatment.isNotBlank()) descriptionParts.add("치료내용: $treatment")
                        if (medication.isNotBlank()) descriptionParts.add("투약내용: $medication")
                        if (veterinarian.isNotBlank()) descriptionParts.add("담당수의사: $veterinarian")

                        val combinedDescription = descriptionParts.joinToString("\n").ifBlank { null }

                        val healthRecord = HealthRecord(
                            id = "",
                            petId = pet.id,
                            type = selectedType,
                            title = title,
                            description = combinedDescription,
                            date = selectedDate,
                            veterinaryClinic = hospital.ifBlank { null },
                            nextAppointment = null,
                            medication = medication.ifBlank { null },
                            cost = null,
                            notes = notes.ifBlank { null }
                        )
                        onSave(healthRecord)
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = GrayLight
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "저장하기",
                    fontFamily = NotoSansKR,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }

    
    if (showDatePicker) {
        
        
    }
}
