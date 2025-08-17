package com.example.ppet.service

import com.example.ppet.data.model.*
import com.example.ppet.model.Quest
import com.example.ppet.model.QuestCategory
import com.example.ppet.model.QuestStatus
import com.example.ppet.model.QuestType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 현재 사용자 ID 가져오기
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    // 사용자 정보 저장
    suspend fun saveUserInfo(userInfo: UserInfo): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            val firebaseUser = FirebaseUser(
                id = userId,
                displayName = userInfo.displayName,
                email = userInfo.email,
                profilePictureUrl = userInfo.profilePictureUrl
            )

            database.child("users").child(userId).setValue(firebaseUser).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 사용자 정보 업데이트
    suspend fun updateUserInfo(updates: Map<String, Any>): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val updatedData = updates + ("updatedAt" to System.currentTimeMillis())

            database.child("users").child(userId).updateChildren(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 펫 정보 저장
    suspend fun savePet(pet: Pet): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            val firebasePet = FirebasePet(
                id = pet.id,
                name = pet.name,
                type = pet.type,
                breed = pet.breed,
                age = pet.age,
                weight = pet.weight,
                gender = pet.gender,
                birthDate = pet.birthDate?.time,
                imageUrl = pet.imageUrl,
                characterId = pet.characterId,
                isNeutered = pet.isNeutered,
                allergies = pet.allergies,
                notes = pet.notes,
                ownerId = userId
            )

            database.child("pets").child(pet.id).setValue(firebasePet).await()

            // 사용자의 펫 목록에도 추가
            database.child("userPets").child(userId).child(pet.id).setValue(true).await()

            Result.success(pet.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 사용자의 펫 목록 가져오기
    fun getUserPets(): Flow<List<FirebasePet>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pets = mutableListOf<FirebasePet>()
                for (petSnapshot in snapshot.children) {
                    val pet = petSnapshot.getValue(FirebasePet::class.java)
                    if (pet != null && pet.ownerId == userId) {
                        pets.add(pet)
                    }
                }
                trySend(pets)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.child("pets").addValueEventListener(listener)

        awaitClose {
            database.child("pets").removeEventListener(listener)
        }
    }

    // 건강 기록 저장
    suspend fun saveHealthRecord(healthRecord: HealthRecord): Result<String> {
        return try {
            val firebaseRecord = FirebaseHealthRecord(
                id = healthRecord.id,
                petId = healthRecord.petId,
                type = healthRecord.type.name,
                title = healthRecord.title,
                description = healthRecord.description,
                date = healthRecord.date.time,
                veterinaryClinic = healthRecord.veterinaryClinic,
                nextAppointment = healthRecord.nextAppointment?.time,
                medication = healthRecord.medication,
                cost = healthRecord.cost,
                notes = healthRecord.notes
            )

            database.child("healthRecords").child(healthRecord.id).setValue(firebaseRecord).await()

            // 펫별 건강기록 인덱스 업데이트
            database.child("petHealthRecords").child(healthRecord.petId)
                .child(healthRecord.id).setValue(true).await()

            Result.success(healthRecord.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 퀘스트 저장
    suspend fun saveQuest(quest: Quest): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            val firebaseQuest = FirebaseQuest(
                id = quest.id,
                title = quest.title,
                description = quest.description,
                category = quest.category.name,
                type = quest.type.name,
                targetValue = quest.targetValue,
                currentProgress = quest.currentProgress,
                unit = quest.unit,
                rewardExp = quest.rewardExp,
                rewardCoins = quest.rewardCoins,
                startDate = quest.startDate.time,
                endDate = quest.endDate.time,
                status = quest.status.name,
                isAutoDetectable = quest.isAutoDetectable,
                petId = quest.petId,
                userId = userId
            )

            database.child("quests").child(quest.id).setValue(firebaseQuest).await()

            // 사용자별 퀘스트 인덱스 업데이트
            database.child("userQuests").child(userId).child(quest.id).setValue(true).await()

            Result.success(quest.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 퀘스트 진행도 업데이트
    suspend fun updateQuestProgress(questId: String, progress: Int): Result<Unit> {
        return try {
            val updates = mapOf(
                "currentProgress" to progress,
                "updatedAt" to System.currentTimeMillis()
            )

            database.child("quests").child(questId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 퀘스트 완료 처리
    suspend fun completeQuest(questId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to QuestStatus.COMPLETED.name,
                "currentProgress" to database.child("quests").child(questId).child("targetValue"),
                "updatedAt" to System.currentTimeMillis()
            )

            database.child("quests").child(questId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 활동 기록 저장 (산책, 급식 등)
    suspend fun saveActivityRecord(
        petId: String,
        activityType: String,
        duration: Int? = null,
        distance: Double? = null,
        notes: String? = null,
        location: String? = null
    ): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val recordId = database.child("activityRecords").push().key
                ?: return Result.failure(Exception("Failed to generate record ID"))

            val activityRecord = FirebaseActivityRecord(
                id = recordId,
                petId = petId,
                userId = userId,
                type = activityType,
                duration = duration,
                distance = distance,
                notes = notes,
                location = location
            )

            database.child("activityRecords").child(recordId).setValue(activityRecord).await()

            // 펫별 활동기록 인덱스 업데이트
            database.child("petActivityRecords").child(petId).child(recordId).setValue(true).await()

            Result.success(recordId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 알림 저장
    suspend fun saveNotification(
        title: String,
        message: String,
        type: String,
        petId: String? = null,
        actionData: Map<String, String>? = null
    ): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val notificationId = database.child("notifications").push().key
                ?: return Result.failure(Exception("Failed to generate notification ID"))

            val notification = FirebaseNotification(
                id = notificationId,
                title = title,
                message = message,
                type = type,
                userId = userId,
                petId = petId,
                actionData = actionData
            )

            database.child("notifications").child(notificationId).setValue(notification).await()

            // 사용자별 알림 인덱스 업데이트
            database.child("userNotifications").child(userId).child(notificationId).setValue(true).await()

            Result.success(notificationId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 사용자 레벨/경험치 업데이트
    suspend fun updateUserLevel(expGain: Int, coinGain: Int = 0): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            // 현재 사용자 정보 가져오기
            val userSnapshot = database.child("users").child(userId).get().await()
            val currentUser = userSnapshot.getValue(FirebaseUser::class.java)
                ?: return Result.failure(Exception("User data not found"))

            val newTotalExp = currentUser.totalExp + expGain
            val newCoins = currentUser.coins + coinGain

            // 레벨 계산 (예: 100exp마다 레벨업)
            val newLevel = (newTotalExp / 100) + 1

            val updates = mapOf(
                "totalExp" to newTotalExp,
                "level" to newLevel,
                "coins" to newCoins,
                "updatedAt" to System.currentTimeMillis()
            )

            database.child("users").child(userId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 데이터 삭제 메서드들
    suspend fun deletePet(petId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            // 펫 데이터 삭제
            database.child("pets").child(petId).removeValue().await()

            // 사용자 펫 목록에서 제거
            database.child("userPets").child(userId).child(petId).removeValue().await()

            // 관련 건강기록, 활동기록 등도 삭제 (옵션)
            database.child("petHealthRecords").child(petId).removeValue().await()
            database.child("petActivityRecords").child(petId).removeValue().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteHealthRecord(recordId: String, petId: String): Result<Unit> {
        return try {
            database.child("healthRecords").child(recordId).removeValue().await()
            database.child("petHealthRecords").child(petId).child(recordId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
