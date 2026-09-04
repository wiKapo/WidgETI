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
    val group: Char? = null,
    val beginDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val repeat: Int = 1, // TODO Figure out better name
)

//@Serializable
//data class Lesson(
//    val subjectId: String = "",
//    val name: String = "",
//    val kind: Char = '#',
//    val teacher: String = "",
//    val place: String = "",
//    val startHour: Int = 0,
//    val endHour: Int = 0,
//)
