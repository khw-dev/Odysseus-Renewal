package com.example.ppet.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ppet.model.PetCharacter
import com.example.ppet.model.PetCharacters
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "ppet_user_prefs", Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        private const val KEY_SELECTED_CHARACTER = "selected_character"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_LEVEL = "user_level"
        private const val KEY_USER_EXP = "user_exp"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    }

    // 캐릭터 관련 메서드
    suspend fun saveSelectedCharacter(character: PetCharacter) {
        val characterJson = gson.toJson(character)
        sharedPreferences.edit()
            .putString(KEY_SELECTED_CHARACTER, characterJson)
            .apply()
    }

    suspend fun getCurrentCharacter(): PetCharacter? {
        val characterJson = sharedPreferences.getString(KEY_SELECTED_CHARACTER, null)
        return if (characterJson != null) {
            try {
                gson.fromJson(characterJson, PetCharacter::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // 사용자 정보 관련 메서드
    fun saveUserName(name: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
    }

    fun saveUserLevel(level: Int) {
        sharedPreferences.edit()
            .putInt(KEY_USER_LEVEL, level)
            .apply()
    }

    fun getUserLevel(): Int {
        return sharedPreferences.getInt(KEY_USER_LEVEL, 1)
    }

    fun saveUserExp(exp: Int) {
        sharedPreferences.edit()
            .putInt(KEY_USER_EXP, exp)
            .apply()
    }

    fun getUserExp(): Int {
        return sharedPreferences.getInt(KEY_USER_EXP, 0)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_NOTIFICATION_ENABLED, enabled)
            .apply()
    }

    fun isNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }
}
