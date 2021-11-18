package com.bartovapps.gate_opener.core.formatters

import android.content.res.Resources
import com.bartovapps.gate_opener.R
import javax.inject.Inject

class SpeedFormatter @Inject constructor(private val resources: Resources): Formatter<Double> {
    override fun format(value: Double): String {
        return if(value > 1000){
            resources.getString(R.string.km_format, value)
        } else {
            resources.getString(R.string.meters_format, value)
        }
    }
}