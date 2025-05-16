package com.una.exam.mappers

import com.una.exam.models.Student
import com.una.exam.models.StudentEntity


fun StudentEntity.toStudent(): Student {
    return Student(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        courseId = this.courseId // <- correcto
    )
}


fun Student.toEntity(): StudentEntity {
    return StudentEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        courseId = this.courseId
    )
}