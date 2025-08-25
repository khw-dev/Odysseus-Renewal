package com.example.ppet.data.repository

import com.example.ppet.data.model.*
import com.example.ppet.model.Quest
import com.example.ppet.service.FirebaseService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PpetRepository @Inject constructor(
    private val firebaseService: FirebaseService
) {

    
    suspend fun saveUserInfo(userInfo: UserInfo): Result<Unit> {
        return firebaseService.saveUserInfo(userInfo)
    }

    suspend fun updateUserProfile(updates: Map<String, Any>): Result<Unit> {
        return firebaseService.updateUserInfo(updates)
    }

    suspend fun updateUserLevel(expGain: Int, coinGain: Int = 0): Result<Unit> {
        return firebaseService.updateUserLevel(expGain, coinGain)
    }

    
    suspend fun savePet(pet: Pet): Result<String> {
        return firebaseService.savePet(pet)
    }

    fun getUserPets(): Flow<List<FirebasePet>> {
        return firebaseService.getUserPets()
    }

    suspend fun deletePet(petId: String): Result<Unit> {
        return firebaseService.deletePet(petId)
    }

    
    suspend fun saveHealthRecord(healthRecord: HealthRecord): Result<String> {
        return firebaseService.saveHealthRecord(healthRecord)
    }

    suspend fun deleteHealthRecord(recordId: String, petId: String): Result<Unit> {
        return firebaseService.deleteHealthRecord(recordId, petId)
    }

    
    suspend fun saveQuest(quest: Quest): Result<String> {
        return firebaseService.saveQuest(quest)
    }

    suspend fun updateQuestProgress(questId: String, progress: Int): Result<Unit> {
        return firebaseService.updateQuestProgress(questId, progress)
    }

    suspend fun completeQuest(questId: String): Result<Unit> {
        return firebaseService.completeQuest(questId)
    }

    
    suspend fun saveWalkRecord(
        petId: String,
        duration: Int,
        distance: Double,
        location: String? = null,
        notes: String? = null
    ): Result<String> {
        return firebaseService.saveActivityRecord(
            petId = petId,
            activityType = "WALK",
            duration = duration,
            distance = distance,
            location = location,
            notes = notes
        )
    }

    suspend fun saveFeedingRecord(
        petId: String,
        notes: String? = null
    ): Result<String> {
        return firebaseService.saveActivityRecord(
            petId = petId,
            activityType = "FEEDING",
            notes = notes
        )
    }

    suspend fun savePlayRecord(
        petId: String,
        duration: Int,
        notes: String? = null
    ): Result<String> {
        return firebaseService.saveActivityRecord(
            petId = petId,
            activityType = "PLAY",
            duration = duration,
            notes = notes
        )
    }

    
    suspend fun saveNotification(
        title: String,
        message: String,
        type: String,
        petId: String? = null,
        actionData: Map<String, String>? = null
    ): Result<String> {
        return firebaseService.saveNotification(title, message, type, petId, actionData)
    }

    
    suspend fun completeQuestAndUpdateUser(questId: String, expReward: Int, coinReward: Int): Result<Unit> {
        return try {
            
            firebaseService.completeQuest(questId).getOrThrow()

            
            firebaseService.updateUserLevel(expReward, coinReward).getOrThrow()

            
            firebaseService.saveNotification(
                title = "퀘스트 완료!",
                message = "경험치 $expReward, 코인 $coinReward 를 획득했습니다!",
                type = "QUEST_COMPLETED"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
