package com.wikapo.widgeti.util

import java.time.DayOfWeek
import java.time.LocalDate

fun LocalDate.nextDay(showWeekends: Boolean): LocalDate {
    var next = this.plusDays(1)
    if (!showWeekends)
        while (next.dayOfWeek == DayOfWeek.SATURDAY || next.dayOfWeek == DayOfWeek.SUNDAY)
            next = next.plusDays(1)
    return next
}

fun LocalDate.previousDay(showWeekends: Boolean): LocalDate {
    var prev = this.minusDays(1)
    if (!showWeekends)
        while (prev.dayOfWeek == DayOfWeek.SATURDAY || prev.dayOfWeek == DayOfWeek.SUNDAY)
            prev = prev.minusDays(1)
    return prev
}

fun today(showWeekends: Boolean): LocalDate {
    var today = LocalDate.now()
    if (!showWeekends)
        while (today.dayOfWeek == DayOfWeek.SATURDAY || today.dayOfWeek == DayOfWeek.SUNDAY)
            today = today.plusDays(1)
    return today
}