package com.example.ppet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.R
import com.example.ppet.ui.around.AroundScreen
import com.example.ppet.ui.home.HomeScreen
import com.example.ppet.ui.home.viewmodel.PetViewModel
import com.example.ppet.ui.profile.ProfileScreen
import com.example.ppet.ui.quest.QuestScreen
import com.example.ppet.ui.theme.BackgroundGray
import com.example.ppet.ui.theme.GrayLight
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary

data class TabItem(
    val title: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(
    userInfo: com.example.ppet.auth.UserInfo? = null,
    onSignOut: () -> Unit = {}
) {
    val tabs = listOf(
        TabItem("홈", R.drawable.ic_home),
        TabItem("주변", R.drawable.ic_location),
        TabItem("퀘스트", R.drawable.ic_quest),
        TabItem("프로필", R.drawable.ic_profile)
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddPetScreen by remember { mutableStateOf(false) }
    var showHealthRecordScreen by remember { mutableStateOf(false) }
    var showAddHealthRecordScreen by remember { mutableStateOf(false) }
    var showNotificationScreen by remember { mutableStateOf(false) }
    var showLocationSettingScreen by remember { mutableStateOf(false) }
    var selectedPetForHealthRecord by remember { mutableStateOf<com.example.ppet.data.model.Pet?>(null) }

    val petViewModel: PetViewModel = hiltViewModel()

    if (showAddPetScreen) {
        com.example.ppet.ui.pet.AddPetScreen(
            onBack = { showAddPetScreen = false },
            onSavePet = { pet ->
                petViewModel.addPet(pet)
                showAddPetScreen = false
            }
        )
    } else if (showAddHealthRecordScreen && selectedPetForHealthRecord != null) {
        com.example.ppet.ui.health.AddHealthRecordScreen(
            pet = selectedPetForHealthRecord!!,
            onBack = {
                showAddHealthRecordScreen = false
                selectedPetForHealthRecord = null
            },
            onSaveRecord = { healthRecord ->
                petViewModel.addHealthRecord(healthRecord)
                showAddHealthRecordScreen = false
                selectedPetForHealthRecord = null
            }
        )
    } else if (showHealthRecordScreen && selectedPetForHealthRecord != null) {
        val healthRecords by petViewModel.healthRecords.collectAsState()

        com.example.ppet.ui.health.HealthRecordScreen(
            pet = selectedPetForHealthRecord!!,
            healthRecords = healthRecords,
            onBack = {
                showHealthRecordScreen = false
                selectedPetForHealthRecord = null
            },
            onAddRecord = {
                showHealthRecordScreen = false
                showAddHealthRecordScreen = true
            },
            onRecordClick = { healthRecord ->
                // TODO: 건강 기록 상세 화면으로 이동 (추후 구현)
            }
        )
    } else if (showNotificationScreen) {
        com.example.ppet.ui.notification.NotificationScreen(
            notifications = emptyList(), // 실제 구현에서는 NotificationViewModel에서 가져옴
            onBack = { showNotificationScreen = false },
            onNotificationClick = { },
            onMarkAsRead = { },
            onMarkAllAsRead = { },
            onDeleteNotification = { }
        )
    } else if (showLocationSettingScreen) {
        com.example.ppet.ui.location.LocationSettingScreen(
            currentLocation = null,
            onBack = { showLocationSettingScreen = false },
            onLocationSelected = { },
            onCurrentLocationRequest = { }
        )
    } else {
        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        ),
                    color = BackgroundGray,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        NavigationBar(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .border(
                                    width = 1.dp,
                                    color = GrayLight,
                                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                ),
                            containerColor = BackgroundGray,
                            contentColor = Color.Black
                        ) {
                            tabs.forEachIndexed { index, tab ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = tab.iconRes),
                                            contentDescription = tab.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontFamily = NotoSansKR,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = OrangePrimary,
                                        selectedTextColor = OrangePrimary,
                                        unselectedIconColor = Color.Black,
                                        unselectedTextColor = Color.Black,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
            ) {
                when (selectedTabIndex) {
                    0 -> HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        userName = userInfo?.displayName,
                        onNavigateToAround = { selectedTabIndex = 1 },
                        onNavigateToAddPet = { showAddPetScreen = true },
                        onNavigateToHealthRecord = { pet ->
                            selectedPetForHealthRecord = pet
                            showHealthRecordScreen = true
                        },
                        onNavigateToNotification = { showNotificationScreen = true },
                        onNavigateToLocationSetting = { showLocationSettingScreen = true }
                    )
                    1 -> AroundScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> QuestScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                    3 -> ProfileScreen(
                        modifier = Modifier.fillMaxSize(),
                        userName = userInfo?.displayName,
                        userEmail = userInfo?.email,
                        userProfileImage = userInfo?.profilePictureUrl,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}
