package com.example.openskyapicase.di

import com.example.openskyapicase.data.remote.datasource.FlightRemoteDataSource
import com.example.openskyapicase.data.remote.datasource.FlightRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFlightRemoteDataSource(
        impl: FlightRemoteDataSourceImpl
    ): FlightRemoteDataSource
}