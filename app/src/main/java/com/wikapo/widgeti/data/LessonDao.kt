package com.wikapo.widgeti.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface LessonDao {
    @Query("SELECT * FROM lesson")
    suspend fun getAll(): List<Lesson>

    @Insert
    suspend fun insertAll(vararg lessons: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)

    @Query("DELETE FROM lesson")
    suspend fun deleteAll()
}