package com.wikapo.widgeti

import android.util.Log
import com.wikapo.widgeti.data.Lesson
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun parseSchedule(htmlContent: String?): List<Lesson>? {
    if (htmlContent.isNullOrBlank()) return null
    val document = Jsoup.parse(htmlContent)
    val rows = document.body().getElementsByTag("tr")

    val schedule: MutableList<Lesson> = mutableListOf()
    rows.forEach { row ->
        val cells = row.getElementsByTag("td")
        cells.next()

        var time: LocalTime = LocalTime.MIN
        cells.forEachIndexed { index, cell ->
            if (index == 0) {
                time = LocalTime.parse(cell.text())
                return@forEachIndexed
            }
            var lessonIndex = 0
            val rawLessons: MutableList<MutableMap<String, Any?>> = mutableListOf(mutableMapOf())

            fun parseBoldedElements(boldedElements: List<Element>) {
                boldedElements.forEach { element ->
                    element.text().split(";").map { it.trim() }.forEach {
                        when {
                            it.contains("""\[\w]""".toRegex()) -> rawLessons[lessonIndex]["kind"] =
                                it[1]

                            it.contains("""(\d{1,2}\.){2}\d{2,4}""".toRegex()) || it == "zajęcia wprowadzające dnia" -> {
                                val date = parseDate(it.removePrefix("zajęcia wprowadzające dnia "))
                                rawLessons[lessonIndex]["endDate"] = date
                                rawLessons[lessonIndex]["beginDate"] = date
                            }

                            it.startsWith("gr.") -> rawLessons[lessonIndex]["group"] =
                                it.removePrefix("gr.").first().uppercaseChar()

                            it.startsWith("od") -> rawLessons[lessonIndex]["beginDate"] =
                                parseDate(it.removePrefix("od "))

                            it.startsWith("do") -> rawLessons[lessonIndex]["endDate"] =
                                parseDate(it.removePrefix("do "))

                            it == "remote classes" || it == "przedmiot obieralny" || it == "zajęcia nieregularne" -> rawLessons[lessonIndex]["extra"] =
                                rawLessons[lessonIndex]["extra"].toString() + it

                            it == "first half semester" -> rawLessons[lessonIndex]["extra"] =
                                rawLessons[lessonIndex]["extra"].toString() + "PICK END DATE" + it //TODO let user pick end date

                            it == "co 2 tygodnie" -> rawLessons[lessonIndex]["periodicity"] = 2

                            it == "zajęcia w dniach: " -> rawLessons[lessonIndex]["extra"] =
                                rawLessons[lessonIndex]["extra"].toString() + it //TODO Handle specific dates set

                            it.contains("""^(.+\d+)$|^.*AUD.*$""".toRegex()) -> {
                                if (rawLessons[lessonIndex]["place"] != null) lessonIndex++
                                rawLessons[lessonIndex]["place"] = it
                            }

                            else -> rawLessons[lessonIndex]["extra"] =
                                rawLessons[lessonIndex]["extra"].toString() + "NOT RECOGNIZED" + it
                        }
                    }
                }
            }

            if (cell.text() != "") {
                cell.getElementsByClass("subject_name").eachText().forEachIndexed { index, name ->
                    if (index == rawLessons.size) rawLessons.add(mutableMapOf())
                    rawLessons[index]["name"] = name
                }
                cell.wholeOwnText().split("\n").filter { it.isNotBlank() }
                    .forEachIndexed { index, teacher ->
                        rawLessons[index]["teacher"] = teacher
                    }

                parseBoldedElements(cell.getElementsByTag("b"))

                rawLessons.forEach { rawLesson ->
                    val lesson = Lesson(
                        name = rawLesson["name"] as String,
                        kind = rawLesson["kind"] as Char,
                        teacher = rawLesson["teacher"] as String,
                        place = rawLesson["place"] as String,
                        weekDay = index - 1,
                        startTime = time,
                        endTime = time.plusHours(1),
                        group = rawLesson["group"] as Char?,
                        beginDate = rawLesson["beginDate"] as LocalDate?,
                        endDate = rawLesson["endDate"] as LocalDate?,
                        periodicity = if (rawLesson["periodicity"] == null) 1 else rawLesson["periodicity"] as Int,
                        extra = rawLesson["extra"] as String?
                    )
                    Log.d("READ LESSON", lesson.toString())
                    schedule.add(lesson)
                }
            }
        }
    }

    return schedule
}

private fun parseDate(dateString: String): LocalDate? {
    return try {
        LocalDate.parse(dateString, dateFormatter)
    } catch (e: Exception) {
        null
    }
}
