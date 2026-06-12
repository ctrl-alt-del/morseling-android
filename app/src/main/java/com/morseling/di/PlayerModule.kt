package com.morseling.di

import com.morseling.audio.FlashlightMorsePlayer
import com.morseling.audio.MorseCodeConverter
import com.morseling.audio.MorsePlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    @Named("audio")
    abstract fun bindAudioPlayer(converter: MorseCodeConverter): MorsePlayer

    @Binds
    @Singleton
    @Named("flash")
    abstract fun bindFlashPlayer(player: FlashlightMorsePlayer): MorsePlayer
}
