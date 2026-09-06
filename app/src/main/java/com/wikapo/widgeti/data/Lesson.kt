package com.wikapo.widgeti.data

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class Lesson(
    val name: String,
    val kind: Char,
    val teacher: String,
    val place: String,
    val weekDay: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val group: Char?,
    val beginDate: LocalDate?,
    val endDate: LocalDate?,
    val periodicity: Int, //How often is such lesson 1 week, 2 weeks or more
    val extra: String?, //Unparsed data
)