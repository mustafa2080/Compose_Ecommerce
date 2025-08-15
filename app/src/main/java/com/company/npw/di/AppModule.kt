package com.company.npw.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.company.npw.data.local.preferences.PreferencesManager
import com.company.npw.data.remote.firebase.auth.FirebaseAuthService
import com.company.npw.data.remote.firebase.database.FirebaseDatabaseService
import com.company.npw.data.remote.firebase.storage.FirebaseStorageService
import com.company.npw.data.seeder.DatabaseSeeder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        val database = FirebaseDatabase.getInstance()
        // Enable offline persistence
        database.setPersistenceEnabled(true)
        return database
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providePreferencesManager(dataStore: DataStore<Preferences>): PreferencesManager {
        return PreferencesManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthService(
        firebaseAuth: FirebaseAuth,
        firebaseDatabase: FirebaseDatabase
    ): FirebaseAuthService {
        return FirebaseAuthService(firebaseAuth, firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabaseService(
        firebaseDatabase: FirebaseDatabase
    ): FirebaseDatabaseService {
        return FirebaseDatabaseService(firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageService(
        firebaseStorage: FirebaseStorage
    ): FirebaseStorageService {
        return FirebaseStorageService(firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideDatabaseSeeder(
        firebaseDatabase: FirebaseDatabase
    ): DatabaseSeeder {
        return DatabaseSeeder(firebaseDatabase)
    }
}
