package com.bartovapps.gate_opener.di

import android.content.Context
import com.bartovapps.gate_opener.analytics.endpoint.AnalyticsEndpoint
import com.bartovapps.gate_opener.analytics.endpoint.FirebaseEndpoint
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.analytics.manager.AnalyticsManager
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    @QFirebaseAnalyticsEndpoint
    abstract fun bindFirebaseEndpoint(endpoint: FirebaseEndpoint) : AnalyticsEndpoint

    companion object{
        @Provides
        @Singleton
        fun provideAnalyticsManager(@QFirebaseAnalyticsEndpoint endpoint: AnalyticsEndpoint) : Analytics {
            return AnalyticsManager(listOf(endpoint))
        }

        @Provides
        fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics{
            return FirebaseAnalytics.getInstance(context)
        }
    }
}