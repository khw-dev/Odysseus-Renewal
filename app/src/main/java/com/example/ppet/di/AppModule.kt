package com.example.ppet.di

import android.content.Context
import com.example.ppet.auth.GoogleSignInManager
import com.example.ppet.data.repository.PpetRepository
import com.example.ppet.repository.PetRepository
import com.example.ppet.repository.QuestRepository
import com.example.ppet.repository.UserRepository
import com.example.ppet.service.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun providePpetRepository(
        firebaseService: FirebaseService
    ): PpetRepository {
        return PpetRepository(firebaseService)
    }

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

    @Provides
    @Singleton
    fun provideGoogleSignInManager(
        @ApplicationContext context: Context,
        repository: PpetRepository
    ): GoogleSignInManager {
        return GoogleSignInManager(context, repository)
    }
}
