package com.example.faceimage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Query("SELECT * FROM ImageEntity")
    suspend fun getAllImages(): List<ImageEntity>

    @Query("SELECT * FROM ImageEntity WHERE imagePath = :imagePath")
    suspend fun getImageByPath(imagePath: String): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)
}