package com.morseling.di

import android.content.Context
import androidx.room.Room
import com.morseling.data.ConversionDatabase
import com.morseling.data.ConversionHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ConversionDatabase =
        Room.databaseBuilder(context, ConversionDatabase::class.java, "morseling.db").build()

    @Provides
    fun provideConversionHistoryDao(database: ConversionDatabase): ConversionHistoryDao =
        database.conversionHistoryDao()
}
