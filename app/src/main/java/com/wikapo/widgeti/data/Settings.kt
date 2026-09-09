package com.wikapo.widgeti.data

data class Settings(
    val showBreaks: Boolean = false,
    val showWeekends: Boolean = false,
    val scheduleName: String? = null
) {
    constructor(allBooleans: Boolean) : this(allBooleans, allBooleans)
}