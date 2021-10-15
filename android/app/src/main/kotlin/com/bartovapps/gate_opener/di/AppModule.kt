package com.bartovapps.gate_opener.di

import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectionProcessor
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectionProcessorImpl
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetector
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetectorImpl
import com.bartovapps.gate_opener.core.dialer.Dialer
import com.bartovapps.gate_opener.core.dialer.DialerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}