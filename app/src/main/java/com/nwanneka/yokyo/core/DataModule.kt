package com.nwanneka.yokyo.core

import android.content.Context
import com.nwanneka.yokyo.data.SharedPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context) = SharedPreferenceManager(context)

}