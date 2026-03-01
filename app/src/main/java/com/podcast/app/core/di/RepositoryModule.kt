package com.podcast.app.core.di

import com.podcast.app.data.repository.HomeRepositoryImpl
import com.podcast.app.data.repository.SearchRepositoryImpl
import com.podcast.app.domain.repository.HomeRepository
import com.podcast.app.domain.repository.SearchRepository
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
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}
