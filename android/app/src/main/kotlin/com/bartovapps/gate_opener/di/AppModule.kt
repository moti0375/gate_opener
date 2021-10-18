package com.bartovapps.gate_opener.di

import android.content.Context
import androidx.room.Room
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectionProcessor
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectionProcessorImpl
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetector
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectorImpl
import com.bartovapps.gate_opener.core.dialer.Dialer
import com.bartovapps.gate_opener.core.dialer.DialerImpl
import com.bartovapps.gate_opener.data.datasource.GatesDatasource
import com.bartovapps.gate_opener.data.datasource.GatesLocalDatasource
import com.bartovapps.gate_opener.data.repository.GatesRepository
import com.bartovapps.gate_opener.data.repository.GatesRepositoryImpl
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.database.AppDatabase
import com.bartovapps.gate_opener.storage.gates.GatesDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindsCalled(caller: DialerImpl) : Dialer

    @Binds
    abstract fun bindsActivitiesDetectionProcessor(processorImpl: ActivityDetectionProcessorImpl) : ActivityDetectionProcessor

    @Binds
    abstract fun bindsActivityDetector(processorImpl: ActivityDetectorImpl) : ActivityDetector

    @Binds
    abstract fun bindGatesLocalDatasource(gatesLocalDatasource: GatesLocalDatasource) : GatesDatasource

    @Binds
    abstract fun bindsRepository(gatesRepository: GatesRepositoryImpl) : GatesRepository

    companion object{
        @Provides
        fun provideDatabase(@ApplicationContext context: Context) : AppDatabase{
            return Room.databaseBuilder(context, AppDatabase::class.java, "application_db").build()
        }

        @Provides
        fun provideGatesDao(database: AppDatabase): GatesDao{
            return database.gatesDao()
        }
    }
}