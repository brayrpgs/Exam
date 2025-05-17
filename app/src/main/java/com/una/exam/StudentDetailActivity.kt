package com.una.exam

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.una.exam.utils.NetworkUtils

class StudentDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val courseId = intent.getIntExtra("id", -1)
        val courseName = intent.getStringExtra("name") ?: ""
        val courseDescription = intent.getStringExtra("description") ?: ""
        val courseSchedule = intent.getStringExtra("schedule") ?: ""
        val courseProfessor = intent.getStringExtra("professor") ?: ""
        val courseImageUrl = intent.getStringExtra("imageUrl")
        val studentId = intent.getIntExtra("studentId", -1)
        val studentName = intent.getStringExtra("studentName") ?: ""
        val studentEmail = intent.getStringExtra("studentEmail") ?: ""
        val studentPhone = intent.getStringExtra("studentPhone") ?: ""
        val isConnected = NetworkUtils.isNetworkAvailable(this)

        setContent {
            StudentDetailScreen(
                courseName = courseName,
                courseDescription = courseDescription,
                courseSchedule = courseSchedule,
                courseProfessor = courseProfessor,
                studentName = studentName,
                studentEmail = studentEmail,
                studentPhone = studentPhone,
                isConnected = isConnected
            )
        }
    }
}

@Composable
fun StudentDetailScreen(
    courseName: String,
    courseDescription: String,
    courseSchedule: String,
    courseProfessor: String,
    studentName: String,
    studentEmail: String,
    studentPhone: String,
    isConnected: Boolean
) {

    val context = LocalContext.current

    LaunchedEffect(isConnected) {
        val message = if (isConnected) {
            "You're online, showing updated student details"
        } else {
            "You're offline, showing student details in cache"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Course Info",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF69AA72)
            )
            DetailItem(label = "Name", value = courseName)
            DetailItem(label = "Description", value = courseDescription)
            DetailItem(label = "Schedule", value = courseSchedule)
            DetailItem(label = "Professor", value = courseProfessor)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Student Info",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF69AA72)
            )
            DetailItem(label = "Name", value = studentName)
            DetailItem(label = "Email", value = studentEmail)
            DetailItem(label = "Phone", value = studentPhone)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray)
        Text(text = value, fontSize = 18.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
    }
}
