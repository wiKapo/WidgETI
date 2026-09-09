package com.wikapo.widgeti

import android.util.Log
import com.wikapo.widgeti.data.Lesson
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun parseSchedule(htmlContent: String?): Set<Lesson> {
    if (htmlContent.isNullOrBlank()) return emptySet()
    val document = Jsoup.parse(htmlContent)
    val rows = document.body().getElementsByTag("tr")

    val schedule: MutableSet<Lesson> = mutableSetOf()
    rows.drop(1).forEach { row ->
        val cells = row.getElementsByTag("td")

        val time: LocalTime = LocalTime.parse(cells.first()?.text())
        cells.drop(1).forEachIndexed { index, cell ->
            var lessonIndex = 0
            val rawLessons: MutableList<MutableMap<String, Any?>> = mutableListOf(mutableMapOf())
            //TODO change to base on <br>. Think about it

            if (cell.text().isNotBlank()) {
                cell.getElementsByClass("subject_name").eachText().forEachIndexed { index, name ->
                    if (index == rawLessons.size) rawLessons.add(mutableMapOf())
                    rawLessons[index]["name"] = name
                }
                cell.wholeOwnText().split("\n").filter { it.isNotBlank() }
                    .forEachIndexed { index, teacher ->
                        rawLessons[index]["teacher"] = teacher
                    }

                cell.getElementsByTag("b").forEach { element ->
                    element.text().split(";").map { it.trim() }.forEach {
                        when {
                            it.contains("""\[\w]""".toRegex()) -> rawLessons[lessonIndex]["kind"] =
                                it[1]

                            it.contains("""^(\d{1,2}\.){2}\d{2,4}$""".toRegex()) || it == "zajęcia wprowadzające dnia" -> {
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
                                // Start populating new lesson if place was found again
                                if (rawLessons[lessonIndex]["place"] != null) lessonIndex++
                                rawLessons[lessonIndex]["place"] = it
                            }

                            else -> rawLessons[lessonIndex]["extra"] =
                                rawLessons[lessonIndex]["extra"].toString() + "NOT RECOGNIZED" + it
                        }
                    }
                }

                rawLessons.forEach { rawLesson ->
                    val lesson = Lesson(
                        name = rawLesson["name"] as String,
                        kind = rawLesson["kind"] as Char,
                        teacher = rawLesson["teacher"] as String,
                        place = rawLesson["place"] as String,
                        weekDay = index,
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
    mergeLessons(schedule).also { schedule.clear(); schedule.addAll(it) }
    return schedule
}

private fun mergeLessons(lessons: Set<Lesson>): Set<Lesson> {
    val groupedLessons = lessons.groupBy { it.weekDay }
    val result = mutableSetOf<Lesson>()

    groupedLessons.mapValues { (_, lessons) ->
        val sortedLessons =
            lessons.sortedWith(compareBy({ it.weekDay }, { it.startTime }, { it.place }))
        val mergedLessons = mutableListOf<Lesson>()

        sortedLessons.forEach { lesson ->
            val prevLesson = mergedLessons.find {
                it.isMergeableWith(lesson)
            }

            if (prevLesson != null) {
                mergedLessons.remove(prevLesson)
                mergedLessons.add(prevLesson.copy(endTime = lesson.endTime))
            } else {
                mergedLessons.add(lesson)
            }
        }
        result.addAll(mergedLessons)
    }

    return result
}

private fun parseDate(dateString: String): LocalDate? {
    return try {
        LocalDate.parse(dateString, dateFormatter)
    } catch (e: Exception) {
        null
    }
}

fun parseGroupName(htmlContent: String?): String? {
    if (htmlContent.isNullOrBlank()) return null
    val document = Jsoup.parse(htmlContent)
    return document.getElementById("groupName")?.text()
}

fun getExampleSchedule(amount: Int): Set<Lesson> {
    Log.d("Example", "Loading example schedule")
    val schedule: MutableSet<Lesson> = mutableSetOf()
    val formatter = DateTimeFormatter.ofPattern("H:mm")
    for (i in 0..amount) {
        schedule.add(
            Lesson(
                name = "Lekcja $i",
                place = "Sala ${i + 100}",
                teacher = "Aaaa Bbbb",
                kind = 'X',
                weekDay = 0,
                startTime = LocalTime.parse("${(7 + 2 * i + (i % 4 / 3)) % 24}:00", formatter),
                endTime = LocalTime.parse("${(9 + 2 * i) % 24}:00", formatter),
                group = null,
                beginDate = null,
                endDate = null,
                periodicity = 1,
                extra = null
            )
        )
    }
    return schedule
}