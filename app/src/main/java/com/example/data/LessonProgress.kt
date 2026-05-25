package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgress(
    @PrimaryKey val lessonId: Int,
    val theoryRead: Boolean = false,
    val labCompleted: Boolean = false,
    val quizScore: Int = -1, // -1 means not taken/attempted
    val quizPassed: Boolean = false,
    val lastOpened: Long = 0L
)

@Entity(tableName = "global_stats")
data class GlobalStats(
    @PrimaryKey val id: Int = 1, // Single row instance
    val totalComplete: Int = 0,
    val currentStreak: Int = 0,
    val lastOpenedDate: String = "", // formatted "yyyy-MM-dd"
    val lastLessonId: Int = -1
)
