package com.una.exam.models

class Student {
    var id: Int? = null
    var name: String = ""
    var email: String = ""
    var phone: String = ""
    var courseId: Int? = null

    constructor() {}
    constructor(id: Int?, name: String, email: String, phone: String, courseId: Int) {
        this.id = id
        this.name = name
        this.email = email
        this.phone = phone
        this.courseId = courseId

    }
}