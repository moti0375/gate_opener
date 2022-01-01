package com.bartovapps.gate_opener.di

import android.app.AlarmManager
import android.content.Context
import android.location.LocationManager
import androidx.room.Room
import com.bartovapps.gate_opener.core.activators.Activator
import com.bartovapps.gate_opener.core.alarm.AlarmManagerActivator
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectionProcessor
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectionProcessorImpl
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectorImpl
import com.bartovapps.gate_opener.core.alarm.AlarmScheduleCalculator
import com.bartovapps.gate_opener.core.alarm.AlarmScheduleCalculatorImpl
import com.bartovapps.gate_opener.core.alarm.AlarmScheduler
import com.bartovapps.gate_opener.core.dialer.Dialer
import com.bartovapps.gate_opener.core.dialer.DialerImpl
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import com.bartovapps.gate_opener.core.manager.GateOpenerManagerImpl
import com.bartovapps.gate_opener.data.datasource.GatesDatasource
import com.bartovapps.gate_opener.data.datasource.GatesLocalDatasource
import com.bartovapps.gate_opener.data.repository.GatesRepository
import com.bartovapps.gate_opener.data.repository.GatesRepositoryImpl
import com.bartovapps.gate_opener.storage.database.AppDatabase
import com.bartovapps.gate_opener.storage.gates.GatesDao
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindsCalled(caller: DialerImpl) : Dialer

    @Binds
    abstract fun bindsActivitiesDetectionProcessor(processorImpl: ActivityDetectionProcessorImpl) : ActivityDetectionProcessor

    @Binds
    @QActivityDetectorActivator
    abstract fun bindsActivityDetector(processorImpl: ActivityDetectorImpl) : Activator

    @Binds
    abstract fun bindGatesLocalDatasource(gatesLocalDatasource: GatesLocalDatasource) : GatesDatasource

    @Binds
    abstract fun bindsRepository(gatesRepository: GatesRepositoryImpl) : GatesRepository

    @Binds
    abstract fun bindGateOpenerManager(gateOpenerManagerImpl: GateOpenerManagerImpl) : GateOpenerManager

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(scheduler: AlarmManagerActivator) : AlarmScheduler

    @Binds
    abstract fun bindAlarmScheduleCalc(scheduleCalculator: AlarmScheduleCalculatorImpl) : AlarmScheduleCalculator

    companion object{
        @Provides
        fun provideDatabase(@ApplicationContext context: Context) : AppDatabase{
            return Room.databaseBuilder(context, AppDatabase::class.java, "application_db").build()
        }

        @Provides
        fun provideGatesDao(database: AppDatabase): GatesDao{
            return database.gatesDao()
        }

        @Provides
        fun provideLocationManager(@ApplicationContext context: Context) : LocationManager {
            return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        @Provides
        fun provideGeofenceClient(@ApplicationContext context: Context) : GeofencingClient {
            return LocationServices.getGeofencingClient(context)
        }

        @Provides
        @Singleton
        fun provideAlarmManager(@ApplicationContext context: Context) : AlarmManager {
            return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
    }
}