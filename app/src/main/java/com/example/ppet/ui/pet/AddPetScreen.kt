package com.example.ppet.ui.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.data.model.Pet
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    onBack: () -> Unit,
    onSavePet: (Pet) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var isNeutered by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    var showTypeDropdown by remember { mutableStateOf(false) }
    var showGenderDropdown by remember { mutableStateOf(false) }

    val petTypes = listOf("강아지", "고양이", "새", "토끼", "햄스터", "기타")
    val genderTypes = listOf("수컷", "암컷")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "반려동물 추가",
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
            // 이름 입력
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("이름 *", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 동물 종류 선택
            ExposedDropdownMenuBox(
                expanded = showTypeDropdown,
                onExpandedChange = { showTypeDropdown = !showTypeDropdown }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("동물 종류 *", fontFamily = NotoSansKR) },
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
                    petTypes.forEach { petType ->
                        DropdownMenuItem(
                            text = { Text(petType, fontFamily = NotoSansKR) },
                            onClick = {
                                type = petType
                                showTypeDropdown = false
                            }
                        )
                    }
                }
            }

            // 품종 입력
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("품종", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 나이 입력
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("나이 *", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("세", fontFamily = NotoSansKR) }
            )

            // 체중 입력
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("체중", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("kg", fontFamily = NotoSansKR) }
            )

            // 성별 선택
            ExposedDropdownMenuBox(
                expanded = showGenderDropdown,
                onExpandedChange = { showGenderDropdown = !showGenderDropdown }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("성별", fontFamily = NotoSansKR) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = showGenderDropdown
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showGenderDropdown,
                    onDismissRequest = { showGenderDropdown = false }
                ) {
                    genderTypes.forEach { genderType ->
                        DropdownMenuItem(
                            text = { Text(genderType, fontFamily = NotoSansKR) },
                            onClick = {
                                gender = genderType
                                showGenderDropdown = false
                            }
                        )
                    }
                }
            }

            // 중성화 여부
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isNeutered,
                    onCheckedChange = { isNeutered = it },
                    colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "중성화 완료",
                    fontFamily = NotoSansKR,
                    fontSize = 16.sp
                )
            }

            // 메모
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("메모", fontFamily = NotoSansKR) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 저장 버튼
            Button(
                onClick = {
                    if (name.isNotBlank() && type.isNotBlank() && age.isNotBlank()) {
                        val newPet = Pet(
                            id = UUID.randomUUID().toString(),
                            name = name.trim(),
                            type = type,
                            breed = if (breed.isNotBlank()) breed.trim() else null,
                            age = age.toIntOrNull() ?: 0,
                            weight = weight.toDoubleOrNull(),
                            gender = if (gender.isNotBlank()) gender else null,
                            isNeutered = isNeutered,
                            notes = if (notes.isNotBlank()) notes.trim() else null
                        )
                        onSavePet(newPet)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = name.isNotBlank() && type.isNotBlank() && age.isNotBlank()
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
}
