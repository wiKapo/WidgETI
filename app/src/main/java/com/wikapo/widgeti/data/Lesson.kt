package com.wikapo.widgeti.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import java.time.LocalDate
import java.time.LocalTime

@Entity(primaryKeys = ["name", "teacher", "place", "week_day", "start_time"])
data class Lesson(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "kind") val kind: Char,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "place") val place: String,
    @ColumnInfo(name = "week_day") val weekDay: Int,
    @ColumnInfo(name = "start_time") val startTime: LocalTime,
    @ColumnInfo(name = "end_time") val endTime: LocalTime,
    @ColumnInfo(name = "group") val group: Char?,
    @ColumnInfo(name = "begin_date") val beginDate: LocalDate?,
    @ColumnInfo(name = "end_date") val endDate: LocalDate?,
    @ColumnInfo(name = "periodicity") val periodicity: Int, //How often is such lesson 1 week, 2 weeks or more
    @ColumnInfo(name = "extra") val extra: String?, //Unparsed data
) {
    fun isMergeableWith(other: Lesson): Boolean {
        val test =
            (this.name == other.name && this.kind == other.kind && this.teacher == other.teacher
                    && this.place == other.place && this.weekDay == other.weekDay
                    && this.group == other.group && this.beginDate == other.beginDate
                    && this.endDate == other.endDate && this.periodicity == other.periodicity
                    // other is right after this lesson and otherwise is the same
                    && this.endTime == other.startTime)
        if (test)
            return true
        return false
    }
}