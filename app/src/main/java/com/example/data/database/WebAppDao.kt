package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WebAppDao {
    @Query("SELECT * FROM web_apps ORDER BY isFavorite DESC, lastOpenedAt DESC")
    fun getAllWebApps(): Flow<List<WebAppEntity>>

    @Query("SELECT * FROM web_apps WHERE id = :id")
    fun getWebAppByIdFlow(id: String): Flow<WebAppEntity?>

    @Query("SELECT * FROM web_apps WHERE id = :id")
    suspend fun getWebAppById(id: String): WebAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebApp(webApp: WebAppEntity)

    @Update
    suspend fun updateWebApp(webApp: WebAppEntity)

    @Query("DELETE FROM web_apps WHERE id = :id")
    suspend fun deleteWebAppById(id: String)

    @Query("UPDATE web_apps SET lastVisitedUrl = :url, scrollX = :scrollX, scrollY = :scrollY, lastOpenedAt = :timestamp WHERE id = :id")
    suspend fun updateState(id: String, url: String, scrollX: Int, scrollY: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE web_apps SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean)

    @Query("UPDATE web_apps SET launchCount = launchCount + 1, lastOpenedAt = :timestamp WHERE id = :id")
    suspend fun incrementLaunchCount(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM web_apps")
    suspend fun getCount(): Int
}
