package com.una.exam.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.File

@Entity(tableName = "Course")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val schedule: String,
    val professor: String
) {
    @Ignore
    @Transient
    var imageFile: File? = null

    @Ignore
    constructor(
        id: Int?,
        name: String,
        description: String,
        imageUrl: String?,
        schedule: String,
        professor: String,
        imageFile: File?
    ) : this(id, name, description, imageUrl, schedule, professor) {
        this.imageFile = imageFile
    }
}
