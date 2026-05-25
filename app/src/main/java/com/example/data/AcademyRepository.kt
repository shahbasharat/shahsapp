package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AcademyRepository(private val academyDao: AcademyDao) {

    val allProgress: Flow<List<LessonProgress>> = academyDao.getAllProgress()
    val globalStats: Flow<GlobalStats?> = academyDao.getGlobalStats()

    fun getProgressByLessonId(lessonId: Int): Flow<LessonProgress?> {
        return academyDao.getProgressByLessonId(lessonId)
    }

    suspend fun markTheoryRead(lessonId: Int) {
        val existing = academyDao.getProgressByLessonIdSync(lessonId) ?: LessonProgress(lessonId = lessonId)
        val updated = existing.copy(theoryRead = true, lastOpened = System.currentTimeMillis())
        academyDao.insertOrUpdateProgress(updated)
        updateGlobalStatsOnAction(lessonId)
    }

    suspend fun markLabCompleted(lessonId: Int, completed: Boolean) {
        val existing = academyDao.getProgressByLessonIdSync(lessonId) ?: LessonProgress(lessonId = lessonId)
        val updated = existing.copy(labCompleted = completed, lastOpened = System.currentTimeMillis())
        academyDao.insertOrUpdateProgress(updated)
        updateGlobalStatsOnAction(lessonId)
    }

    suspend fun saveQuizScore(lessonId: Int, score: Int, passed: Boolean) {
        val existing = academyDao.getProgressByLessonIdSync(lessonId) ?: LessonProgress(lessonId = lessonId)
        val updated = existing.copy(
            quizScore = score,
            quizPassed = passed,
            lastOpened = System.currentTimeMillis()
        )
        academyDao.insertOrUpdateProgress(updated)
        updateGlobalStatsOnAction(lessonId)
    }

    suspend fun touchLesson(lessonId: Int) {
        val existing = academyDao.getProgressByLessonIdSync(lessonId) ?: LessonProgress(lessonId = lessonId)
        val updated = existing.copy(lastOpened = System.currentTimeMillis())
        academyDao.insertOrUpdateProgress(updated)
        updateGlobalStatsOnAction(lessonId)
    }

    private suspend fun updateGlobalStatsOnAction(lessonId: Int) {
        val existingStats = academyDao.getGlobalStatsSync() ?: GlobalStats()
        
        // Calculate date formats
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Calendar.getInstance().time)
        
        // Streak calculations
        var streak = existingStats.currentStreak
        val lastDateStr = existingStats.lastOpenedDate

        if (lastDateStr.isEmpty()) {
            streak = 1
        } else if (lastDateStr != todayStr) {
            val lastDate = dateFormat.parse(lastDateStr)
            val todayDate = dateFormat.parse(todayStr)
            if (lastDate != null && todayDate != null) {
                val diffMs = todayDate.time - lastDate.time
                val diffDays = diffMs / (1000 * 60 * 60 * 24)
                if (diffDays == 1L) {
                    streak += 1
                } else if (diffDays > 1L) {
                    streak = 1
                }
            } else {
                streak = 1
            }
        }

        // Count completed lessons
        // Completed means theoryRead is true, labCompleted is true, and quizPassed is true
        val allProgressList = academyDao.getAllProgressSync()
        val totalCompleted = allProgressList.count { it.theoryRead && it.labCompleted && it.quizPassed }

        val newStats = existingStats.copy(
            totalComplete = totalCompleted,
            currentStreak = streak,
            lastOpenedDate = todayStr,
            lastLessonId = lessonId
        )
        academyDao.insertOrUpdateGlobalStats(newStats)
    }

    suspend fun clearAllData() {
        academyDao.clearAllProgress()
        academyDao.clearAllStats()
    }

    // Helper to get sync and avoid flow block
    private suspend fun AcademyDao.getAllProgressSync(): List<LessonProgress> {
        // Since we can't easily wait on Flow in simple sync suspends, we can define a room query or just collect first
        return try {
            // Let's create a custom sync query inside AcademyDao to avoid flow complications, see below
            academyDao.getAllProgressSyncQuery()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
