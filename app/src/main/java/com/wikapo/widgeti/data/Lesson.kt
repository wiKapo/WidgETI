package com.wikapo.widgeti.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: Char,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "place") val place: String,
    @ColumnInfo(name = "week_day") val weekDay: Int,
    @ColumnInfo(name = "start_time") val startTime: LocalTime,
    @ColumnInfo(name = "end_time") val endTime: LocalTime,
    @ColumnInfo(name = "group") val group: Char?,
    @ColumnInfo(name = "start_date") val startDate: LocalDate?,
    @ColumnInfo(name = "end_date") val endDate: LocalDate?,
    @ColumnInfo(name = "frequency") val frequency: Int, //How often is such lesson 1 week, 2 weeks or more
    @ColumnInfo(name = "extra") val extra: String?, //Unparsed data
) {
    fun isMergeableWith(other: Lesson): Boolean {
        return this.name == other.name && this.type == other.type && this.teacher == other.teacher
                && this.place == other.place && this.weekDay == other.weekDay
                && this.group == other.group && this.startDate == other.startDate
                && this.endDate == other.endDate && this.frequency == other.frequency
                // other is right after this lesson and otherwise is the same
                && this.endTime == other.startTime
    }

    fun isNotSetUp(): Boolean {
        return this.isBiweeklyAndNotSetUp() || this.endsFirstHalfSemesterAndNotSetUp()
    }

    fun isBiweeklyAndNotSetUp(): Boolean {
        return this.frequency > 1 && this.startDate == null
    }

    fun endsFirstHalfSemesterAndNotSetUp(): Boolean {
        return this.extra?.contains("first half semester") == true && this.endDate == null
    }
}