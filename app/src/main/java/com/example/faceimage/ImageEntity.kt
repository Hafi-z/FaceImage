package com.example.faceimage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageEntity(
    @PrimaryKey
    val imagePath: String,
    val isProcessed: Boolean,
    val isFace: Boolean
)
