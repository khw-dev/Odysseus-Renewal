package com.example.ppet.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.math.*

data class WalkSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Date = Date(),
    var endTime: Date? = null,
    var totalDistance: Float = 0f, // 미터 단위
    var totalDuration: Long = 0L,  // 밀리초 단위
    var isActive: Boolean = true,
    val locations: MutableList<Location> = mutableListOf()
)

data class WalkStats(
    val isWalking: Boolean = false,
    val currentSpeed: Float = 0f, // m/s
    val totalDistance: Float = 0f, // 미터
    val duration: Long = 0L,      // 밀리초
    val averageSpeed: Float = 0f,  // m/s
    val steps: Int = 0            // 추정 걸음수
)

class WalkTrackingService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var currentSession: WalkSession? = null
    private val locationHistory = mutableListOf<Location>()

    private val _walkStats = MutableStateFlow(WalkStats())
    val walkStats: StateFlow<WalkStats> = _walkStats.asStateFlow()

    private val _currentSession = MutableStateFlow<WalkSession?>(null)
    val currentWalkSession: StateFlow<WalkSession?> = _currentSession.asStateFlow()

    companion object {
        private const val LOCATION_INTERVAL = 5000L // 5초마다 위치 업데이트
        private const val FASTEST_INTERVAL = 2000L  // 최소 2초 간격
        private const val MIN_DISTANCE_FOR_UPDATE = 2f // 2미터 이상 이동시만 업데이트
        private const val WALKING_SPEED_THRESHOLD = 0.5f // 0.5m/s 이상을 걷기로 판단
        private const val MAX_WALKING_SPEED = 4.0f // 4m/s 이상은 달리기로 판단
        private const val STATIONARY_TIME_THRESHOLD = 300000L // 5분간 정지시 산책 종료
    }

    inner class LocalBinder : Binder() {
        fun getService(): WalkTrackingService = this@WalkTrackingService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        setupLocationServices()
    }

    private fun setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL)
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
            .setMinUpdateDistanceMeters(MIN_DISTANCE_FOR_UPDATE)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    processLocationUpdate(location)
                }
            }
        }
    }

    fun startWalkTracking(): Boolean {
        if (!hasLocationPermission()) {
            return false
        }

        if (currentSession?.isActive == true) {
            return true // 이미 추적 중
        }

        currentSession = WalkSession().also {
            _currentSession.value = it
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            return true
        } catch (e: SecurityException) {
            return false
        }
    }

    fun stopWalkTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)

        currentSession?.let { session ->
            session.isActive = false
            session.endTime = Date()
            session.totalDuration = session.endTime!!.time - session.startTime.time

            // 세션 저장 (Repository에 저장)
            serviceScope.launch {
                saveWalkSession(session)
            }
        }

        currentSession = null
        _currentSession.value = null
        _walkStats.value = WalkStats()
    }

    private fun processLocationUpdate(location: Location) {
        val session = currentSession ?: return

        locationHistory.add(location)
        session.locations.add(location)

        // 최근 위치 기록 유지 (메모리 절약을 위해 최근 100개만)
        if (locationHistory.size > 100) {
            locationHistory.removeAt(0)
        }

        calculateWalkingMetrics(location)
        detectWalkingState()
    }

    private fun calculateWalkingMetrics(currentLocation: Location) {
        val session = currentSession ?: return

        if (session.locations.size < 2) return

        val previousLocation = session.locations[session.locations.size - 2]
        val distance = currentLocation.distanceTo(previousLocation)
        val timeDiff = currentLocation.time - previousLocation.time

        // 거리 누적
        session.totalDistance += distance

        // 현재 속도 계산 (m/s)
        val currentSpeed = if (timeDiff > 0) {
            distance / (timeDiff / 1000f)
        } else 0f

        // 전체 지속 시간
        val totalDuration = currentLocation.time - session.startTime.time

        // 평균 속도 계산
        val averageSpeed = if (totalDuration > 0) {
            session.totalDistance / (totalDuration / 1000f)
        } else 0f

        // 걸음수 추정 (대략적인 계산)
        val estimatedSteps = (session.totalDistance / 0.7f).toInt() // 평균 보폭 70cm 가정

        _walkStats.value = WalkStats(
            isWalking = isCurrentlyWalking(currentSpeed),
            currentSpeed = currentSpeed,
            totalDistance = session.totalDistance,
            duration = totalDuration,
            averageSpeed = averageSpeed,
            steps = estimatedSteps
        )
    }

    private fun isCurrentlyWalking(speed: Float): Boolean {
        return speed >= WALKING_SPEED_THRESHOLD && speed <= MAX_WALKING_SPEED
    }

    private fun detectWalkingState() {
        val session = currentSession ?: return

        // 최근 위치 기록을 바탕으로 정지 상태 감지
        if (locationHistory.size >= 3) {
            val recentLocations = locationHistory.takeLast(3)
            val isStationary = recentLocations.zipWithNext().all { (prev, current) ->
                prev.distanceTo(current) < MIN_DISTANCE_FOR_UPDATE
            }

            if (isStationary) {
                val lastMovementTime = session.locations.lastOrNull()?.time ?: session.startTime.time
                val stationaryTime = Date().time - lastMovementTime

                // 5분 이상 정지시 자동으로 산책 종료
                if (stationaryTime > STATIONARY_TIME_THRESHOLD) {
                    stopWalkTracking()
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun saveWalkSession(session: WalkSession) {
        // 실제 구현에서는 Repository를 통해 데이터베이스에 저장
        // 여기서는 로그만 출력
        println("산책 세션 저장: ${session.totalDistance}m, ${session.totalDuration/60000}분")
    }

    override fun onDestroy() {
        stopWalkTracking()
        serviceScope.cancel()
        super.onDestroy()
    }
}
