package com.wikapo.widgeti.data

data class Settings(
    val showBreaks: Boolean = false,
    val showWeekends: Boolean = false,
    val scheduleName: String? = null,
    val selectedGroup: String? = null
) {
    constructor(showEverything: Boolean, scheduleName: String? = null, selectedGroup: Char? = null) :
            this(
                showEverything,
                showEverything,
                scheduleName,
                selectedGroup.toString()
            )
}