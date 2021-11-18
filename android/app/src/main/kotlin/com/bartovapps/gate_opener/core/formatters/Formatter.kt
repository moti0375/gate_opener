package com.bartovapps.gate_opener.core.formatters

interface Formatter<T> {
    fun format(value: T) : String
}