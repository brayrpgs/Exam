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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.una.exam.models.Student
import com.una.exam.viewmodel.StudentsViewModel
import kotlinx.coroutines.launch

class StudentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var courseId = intent.getIntExtra("id", -1)
        setContent {
            Background(courseId)
        }
    }
}

@Composable
fun Background(courseId: Int) {
    // Initialize the ViewModel
    val studentsViewModel: StudentsViewModel = viewModel()
    val students = studentsViewModel.students.sortedBy { student -> student.name }
    val isLoading = studentsViewModel.isLoading
    var showDialog by remember { mutableStateOf(false) }
    var studentCreate by remember { mutableStateOf<Student?>(null) }


    // Fetch students when the course ID changes
    LaunchedEffect(courseId) {
        studentsViewModel.fetchStudents(courseId)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    studentCreate = null
                    showDialog = true
                },
                containerColor = Color(0xFF69AA72)
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
            UpdateFetchComponent(courseId)
            Spacer(Modifier.height(20.dp))
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(students) { student ->
                        CardStudent(student = student, showDialog = {
                            showDialog = true
                            studentCreate = student
                        })
                    }
                }
            }
        }

        if (showDialog) {
            DialogStudent(
                student = studentCreate,
                onDismiss = { showDialog = false },
                courseId = courseId,
                studentsViewModel = studentsViewModel
            )
        }
    }
}


@Composable
fun CardStudent(student: Student, showDialog: () -> Unit = {}) {

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = {
            showDialog()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Name : ${student.name}", color = Color(0xFF000000),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left
            )
            Text(
                text = "Email : ${student.email}",
                color = Color(0xFF000000),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left
            )
        }
    }
}

@Composable
fun UpdateFetchComponent(courseId: Int, studentsViewModel: StudentsViewModel = viewModel()) {
    Column {
        Spacer(Modifier.height(20.dp))
        TextButton(
            onClick = { studentsViewModel.fetchStudents(courseId) },
            modifier = Modifier
                .background(Color(0xFF69AA72), CircleShape),
        ) {
            Text("Sync students", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogStudent(
    student: Student?,
    onDismiss: () -> Unit,
    courseId: Int,
    studentsViewModel: StudentsViewModel
) {
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (student == null) "Add new student" else "Edit student",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color(0xFF69AA72),
                        focusedBorderColor = Color(0xFF69AA72),
                        unfocusedBorderColor = Color(0xFF69AA72)
                    )
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color(0xFF69AA72),
                        focusedBorderColor = Color(0xFF69AA72),
                        unfocusedBorderColor = Color(0xFF69AA72)
                    )
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color(0xFF69AA72),
                        focusedBorderColor = Color(0xFF69AA72),
                        unfocusedBorderColor = Color(0xFF69AA72)
                    )
                )

                Row(
                    Modifier
                        .padding(10.dp)
                        .fillMaxWidth(), Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                student?.id?.let {
                                    studentsViewModel.deleteStudent(it, courseId)
                                }
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Delete", color = Color(0xFFFF1744))
                    }
                    TextButton(
                        onClick = { onDismiss() },
                    ) {
                        Text("Cancel", color = Color(0xFF69AA72))
                    }
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (student != null) {
                                    studentsViewModel.updateStudent(
                                        Student(
                                            student.id,
                                            name,
                                            email,
                                            phone,
                                            courseId
                                        )
                                    )
                                } else {
                                    studentsViewModel.insertStudent(
                                        Student(
                                            null,
                                            name,
                                            email,
                                            phone,
                                            courseId
                                        )
                                    )
                                }
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .background(Color(0xFF69AA72), CircleShape),
                        enabled = name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BackgroundPreview() {
    Background(1006)
}





