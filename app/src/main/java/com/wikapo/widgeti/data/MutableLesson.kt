package com.wikapo.widgeti.data

import android.util.Log
import java.time.LocalDate
import java.time.LocalTime


class MutableLesson(
    var id: Int? = null,
    var name: String? = null,
    var type: Char? = null,
    var teacher: String? = null,
    var place: String? = null,
    var weekDay: Int? = null,
    var startTime: LocalTime? = null,
    var endTime: LocalTime? = null,
    var group: Char? = null,
    var startDate: LocalDate? = null,
    var endDate: LocalDate? = null,
    var frequency: Int? = 1, //How often is such lesson (1 week, 2 weeks or more)
    var extra: String? = null, //Unparsed data
) {
    constructor(lesson: Lesson) : this(
        id = lesson.id,
        name = lesson.name,
        type = lesson.type,
        teacher = lesson.teacher,
        place = lesson.place,
        weekDay = lesson.weekDay,
        startTime = lesson.startTime,
        endTime = lesson.endTime,
        group = lesson.group,
        startDate = lesson.startDate,
        endDate = lesson.endDate,
        frequency = lesson.frequency,
        extra = lesson.extra
    )

    fun copy(
        id: Int? = this.id,
        name: String? = this.name,
        type: Char? = this.type,
        teacher: String? = this.teacher,
        place: String? = this.place,
        weekDay: Int? = this.weekDay,
        startTime: LocalTime? = this.startTime,
        endTime: LocalTime? = this.endTime,
        group: Char? = this.group,
        startDate: LocalDate? = this.startDate,
        endDate: LocalDate? = this.endDate,
        frequency: Int? = this.frequency,
        extra: String? = this.extra
    ) = MutableLesson(
        id,
        name,
        type,
        teacher,
        place,
        weekDay,
        startTime,
        endTime,
        group,
        startDate,
        endDate,
        frequency,
        extra
    )

    fun toLesson(): Lesson? {
        try {
            val lesson = Lesson(
                id = id,
                name = name!!,
                type = type!!,
                teacher = teacher!!,
                place = place!!,
                weekDay = weekDay!!,
                startTime = startTime!!,
                endTime = endTime!!,
                group = group,
                startDate = startDate,
                endDate = endDate,
                frequency = frequency!!,
                extra = extra
            )
            return lesson
        } catch (e: Exception) {
            Log.e("Error", "${e.message}\n Caused by: ${e.cause}")
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (other is Lesson)
            return this.toLesson() == other
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (weekDay ?: 0)
        result = 31 * result + group.hashCode()
        result = 31 * result + (frequency ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + teacher.hashCode()
        result = 31 * result + place.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + extra.hashCode()
        return result
    }

    override fun toString(): String {
        return "MutableLesson(id=$id, name=$name, type=$type, teacher=$teacher, place=$place, " +
                "weekDay=$weekDay, startTime=$startTime, endTime=$endTime, group=$group, " +
                "startDate=$startDate, endDate=$endDate, frequency=$frequency, extra=$extra)"
    }
}