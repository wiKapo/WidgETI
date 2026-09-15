package com.wikapo.widgeti.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import java.time.LocalDate

@Dao
interface LessonDao {
    @Query("SELECT * FROM lesson")
    suspend fun getAll(): List<Lesson>

    @Query("SELECT * FROM lesson WHERE week_day = :weekDay")
    suspend fun getByWeekDay(weekDay: Int): List<Lesson>

    @Query(
        "SELECT * FROM lesson WHERE week_day = :weekDay AND " +
                "((start_date IS NULL OR start_date <= :date) AND (end_date IS NULL OR end_date >= :date))"
    )
    suspend fun getByDay(weekDay: Int, date: LocalDate): List<Lesson>

    @Query("SELECT DISTINCT `group` FROM lesson WHERE `group` != ''")
    suspend fun getGroups(): List<Char>

    @Insert
    suspend fun insertAll(vararg lessons: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)

    @Query("DELETE FROM lesson")
    suspend fun deleteAll()
}