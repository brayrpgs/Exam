package com.una.exam

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.una.exam.common.Constants.IMAGES_BASE_URL
import com.una.exam.models.Course
import com.una.exam.ui.theme.ExamTheme
import com.una.exam.viewmodel.CourseViewModel
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.una.exam.network.RetrofitInstance
import com.una.exam.viewmodel.CourseRoomViewModel
import com.una.exam.viewmodel.CourseRoomViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModelRoom: CourseRoomViewModel by viewModels {
        CourseRoomViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetrofitInstance.initCache(applicationContext)
            ExamTheme {
                val courseViewModel: CourseViewModel = viewModel()
                CourseScreen(courseViewModel, viewModelRoom)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(viewModel: CourseViewModel, viewModelRoom: CourseRoomViewModel) {

    val context = LocalContext.current
    val courses by viewModel.courses.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val dataOrigin by viewModelRoom.dataOrigin.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, course ->
            if (course == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchCourses()
                coroutineScope.launch {
                    viewModelRoom.fetchCourse()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(dataOrigin) {
        val message = when (dataOrigin) {
            "LOADING" -> "Loading, please wait..."
            "LOCAL" -> "You're offline, retrieving data from cache..."
            "REMOTE" -> "You're online, fetching data from the internet..."
            else -> "Something went wrong, please try again."
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Courses registered")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedCourse = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new course")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Button with padding
            Button(
                modifier = Modifier
                    .padding(16.dp) // Add padding around the button
                    .fillMaxWidth(),
                onClick = { viewModel.fetchCourses() }
            ) {
                Text("Get last courses")
            }

            // Spacer to ensure some space between button and the list
            Spacer(modifier = Modifier.height(8.dp))

            CourseList(courses, onEdit = { course ->
                selectedCourse = course
                showDialog = true
            }, onDelete = { course -> viewModel.deleteCourse(course.id) })
        }
    }

    if (showDialog) {
        CourseDialog(
            course = selectedCourse,
            onDismiss = { showDialog = false },
            onSave = { course ->
                if (course.id == null) viewModel.addCourse(course) else viewModel.updateCourse(
                    course
                )
                showDialog = false
            }
        )
    }

}

@Composable
fun CourseList(
    courses: List<Course>,
    modifier: Modifier = Modifier,
    onEdit: (Course) -> Unit,
    onDelete: (Course) -> Unit
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(courses) { course ->
            CourseItem(course, onEdit, onDelete)
        }
    }
}

@Composable
fun CourseItem(course: Course, onEdit: (Course) -> Unit, onDelete: (Course) -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(12.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            course.imageUrl?.let {
                RemoteImage(
                    IMAGES_BASE_URL + it
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = course.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Schedule: ${course.schedule}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "Professor: ${course.professor}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextButton(
                    onClick = { onEdit(course) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Edit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = { onDelete(course) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        val intent = Intent(context, StudentsActivity::class.java)
                        intent.putExtra("id", course.id)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF008000))
                ) {
                    Text("Show Students")
                }
            }
        }
    }
}


@Composable
fun RemoteImage(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentScale = ContentScale.Fit
    )
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val fileName = getFileName(contentResolver, uri) ?: return null

    val tempFile = File(context.cacheDir, fileName)
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
    var name: String? = null
    val returnCursor = contentResolver.query(uri, null, null, null, null)
    returnCursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

@Composable
fun CourseDialog(course: Course?, onDismiss: () -> Unit, onSave: (Course) -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(course?.name ?: "") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var schedule by remember { mutableStateOf(course?.schedule ?: "") }
    var professor by remember { mutableStateOf(course?.professor ?: "") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            val file = getFileFromUri(context, it)
            selectedImageFile = file
            file?.let {
                Log.d("FilePath", "Path: ${file.absolutePath}")
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (course == null) "Add new course" else "Edit course") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") })
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") })
                OutlinedTextField(
                    value = schedule,
                    onValueChange = { schedule = it },
                    label = { Text("Schedule") })
                OutlinedTextField(
                    value = professor,
                    onValueChange = { professor = it },
                    label = { Text("Professor") })

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select course image")
                }

                Spacer(modifier = Modifier.height(8.dp))

                imageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Course(
                            course?.id,
                            name,
                            description,
                            selectedImageFile?.name,
                            schedule,
                            professor,
                            selectedImageFile
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.secondary)
            }
        }
    )
}