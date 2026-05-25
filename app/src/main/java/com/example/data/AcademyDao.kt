package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademyDao {
    @Query("SELECT * FROM lesson_progress")
    fun getAllProgress(): Flow<List<LessonProgress>>

    @Query("SELECT * FROM lesson_progress")
    suspend fun getAllProgressSyncQuery(): List<LessonProgress>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    fun getProgressByLessonId(lessonId: Int): Flow<LessonProgress?>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    suspend fun getProgressByLessonIdSync(lessonId: Int): LessonProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: LessonProgress)

    @Query("SELECT * FROM global_stats WHERE id = 1")
    fun getGlobalStats(): Flow<GlobalStats?>

    @Query("SELECT * FROM global_stats WHERE id = 1")
    suspend fun getGlobalStatsSync(): GlobalStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGlobalStats(stats: GlobalStats)

    @Query("DELETE FROM lesson_progress")
    suspend fun clearAllProgress()

    @Query("DELETE FROM global_stats")
    suspend fun clearAllStats()
}
