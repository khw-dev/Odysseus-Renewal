package com.example.ppet.di

import android.content.Context
import com.example.ppet.repository.PetRepository
import com.example.ppet.repository.QuestRepository
import com.example.ppet.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        @ApplicationContext context: Context
    ): UserRepository {
        return UserRepository(context)
    }

    @Provides
    @Singleton
    fun providePetRepository(
        @ApplicationContext context: Context
    ): PetRepository {
        return PetRepository(context)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): com.example.ppet.notification.NotificationManager {
        return com.example.ppet.notification.NotificationManager(context)
    }

    @Provides
    @Singleton
    fun provideQuestRepository(
        @ApplicationContext context: Context
    ): QuestRepository {
        return QuestRepository(context)
    }
}
