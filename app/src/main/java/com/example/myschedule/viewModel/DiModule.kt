package com.example.myschedule.viewModel

import com.example.myschedule.viewModel.MyRepository
import com.example.myschedule.db.MyDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiModule {
    @Singleton
    @Provides
    fun provideNoteRepository(
        myDAO: MyDAO
    ) : MyRepository{
        return MyRepository(myDAO)
    }
}