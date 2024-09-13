package com.karolisstuff.ukcrimemapping.di

import com.karolisstuff.ukcrimemapping.data.repository.CrimeRepositoryImpl
import com.karolisstuff.ukcrimemapping.domain.repository.CrimeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCrimeRepository(
        crimeRepositoryImpl: CrimeRepositoryImpl
    ): CrimeRepository
}
