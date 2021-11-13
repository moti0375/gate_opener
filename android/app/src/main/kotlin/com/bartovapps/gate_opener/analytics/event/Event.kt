package com.bartovapps.gate_opener.analytics.event

import android.os.Bundle
import androidx.core.os.bundleOf

abstract class Event (val name: String, val parameters: Bundle = bundleOf())