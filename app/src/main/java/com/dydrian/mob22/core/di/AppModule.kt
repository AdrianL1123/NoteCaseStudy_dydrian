package com.dydrian.mob22.core.di

import com.dydrian.mob22.data.repo.NoteRepo
import com.dydrian.mob22.data.repo.NoteRepoFirestoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideTaskRepo(): NoteRepo {
        return NoteRepoFirestoreImpl()
    }
}