package com.una.exam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.una.exam.models.Student

class StudentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Background()
        }
    }
}

@Composable
fun Background() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new course")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UpdateFetchComponent()
            Spacer(Modifier.height(20.dp))
            LazyColumn {
                item {
                    CardStudent(student = Student())
                    CardStudent(student = Student())
                    CardStudent(student = Student())
                    CardStudent(student = Student())
                    CardStudent(student = Student())
                    CardStudent(student = Student())
                    CardStudent(student = Student())
                    CardStudent(student = Student())
                }
            }
        }

    }
}


@Composable
fun CardStudent(student: Student) {

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .padding(vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "STUDENT",
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 10.dp),
                color = Color(0x80003CFF),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Name : ${student.name}", color = Color(0x807700FF),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Email : ${student.email}",
                color = Color(0x807700FF),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Phone : ${student.phone}",
                color = Color(0x807700FF),
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = {}) {
                    Text(text = "Edit", color = Color(0xB3FF9800))
                }
                TextButton(onClick = {}) {
                    Text(text = "Delete", color = Color(0xB3FC0000))
                }
            }
        }

    }

}

@Composable
fun UpdateFetchComponent() {
    Column {
        Spacer(Modifier.height(20.dp))
        TextButton(
            onClick = {},
            modifier = Modifier
                .background(Color(0xFF69AA72), CircleShape),
        ) {
            Text("Sync students", color = Color.White)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BackgroundPreview() {
    Background()
}





