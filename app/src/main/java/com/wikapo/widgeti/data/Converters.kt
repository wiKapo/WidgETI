package com.wikapo.widgeti.data

import androidx.room3.ColumnTypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @ColumnTypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.toString()
    }

    @ColumnTypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @ColumnTypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @ColumnTypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}
