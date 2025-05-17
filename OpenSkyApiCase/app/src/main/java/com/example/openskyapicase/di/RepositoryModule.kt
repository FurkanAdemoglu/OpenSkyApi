package com.example.openskyapicase.di

import com.example.openskyapicase.data.remote.datasource.FlightRemoteDataSource
import com.example.openskyapicase.domain.repository.FlightRepository
import com.example.openskyapicase.domain.repository.FlightRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideFlightRepository(
        remoteDataSource: FlightRemoteDataSource
    ): FlightRepository {
        return FlightRepositoryImpl(remoteDataSource)
    }
}