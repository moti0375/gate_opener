package com.bartovapps.gate_opener.di

import android.content.Context
import android.content.res.Resources
import com.bartovapps.gate_opener.core.formatters.Formatter
import com.bartovapps.gate_opener.core.formatters.SpeedFormatter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {
    @Binds
    abstract fun bindsSpeedFormatter(speedFormatter: SpeedFormatter) : Formatter<Double>

    companion object{
        @Provides
        fun provideResources(@ApplicationContext context: Context) : Resources{
            return context.resources
        }
    }
}