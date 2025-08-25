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

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

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

            database.child("userPets").child(userId).child(pet.id).setValue(true).await()

            Result.success(pet.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

            database.child("petHealthRecords").child(healthRecord.petId)
                .child(healthRecord.id).setValue(true).await()

            Result.success(healthRecord.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

            database.child("userQuests").child(userId).child(quest.id).setValue(true).await()

            Result.success(quest.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

            database.child("petActivityRecords").child(petId).child(recordId).setValue(true).await()

            Result.success(recordId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

            database.child("userNotifications").child(userId).child(notificationId).setValue(true).await()

            Result.success(notificationId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserLevel(expGain: Int, coinGain: Int = 0): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            val userSnapshot = database.child("users").child(userId).get().await()
            val currentUser = userSnapshot.getValue(FirebaseUser::class.java)
                ?: return Result.failure(Exception("User data not found"))

            val newTotalExp = currentUser.totalExp + expGain
            val newCoins = currentUser.coins + coinGain

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

    suspend fun deletePet(petId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            database.child("pets").child(petId).removeValue().await()

            database.child("userPets").child(userId).child(petId).removeValue().await()

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

