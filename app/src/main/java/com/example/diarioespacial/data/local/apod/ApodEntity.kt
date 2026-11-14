package com.example.diarioespacial.data.local.apod

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Imagen astronómica del día (APOD) guardada localmente.
 * La fecha (yyyy-MM-dd) identifica cada imagen de forma única.
 */
@Entity(tableName = "apod")
data class ApodEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdurl: String?,
    val mediaType: String,
    val copyright: String?,
    val isFavorite: Boolean = false
)
