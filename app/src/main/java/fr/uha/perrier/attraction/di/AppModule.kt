package fr.uha.perrier.attraction.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.uha.perrier.attraction.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase (@ApplicationContext appContext : Context) = AppDatabase.create(appContext)

    @Singleton
    @Provides
    fun provideAttractionDao (db : AppDatabase) = db.getAttractionDao()

    @Singleton
    @Provides
    fun provideParkDao (db : AppDatabase) = db.getParkDao()

    @Singleton
    @Provides
    fun providePersonDao (db : AppDatabase) = db.getPersonDao()
}