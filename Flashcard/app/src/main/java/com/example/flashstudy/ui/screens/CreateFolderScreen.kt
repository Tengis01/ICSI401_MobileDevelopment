package com.example.flashstudy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.data.Folder
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.White
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderScreen(
    navController: NavController,
    repository: DeckRepository,
    folderId: String?
) {
    val existingFolder = remember(folderId) {
        if (folderId != null) repository.getFolderById(folderId) else null
    }

    var name by remember { mutableStateOf(existingFolder?.name ?: "") }
    var description by remember { mutableStateOf(existingFolder?.description ?: "") }
    var nameError by remember { mutableStateOf(false) }

    fun saveFolder() {
        if (name.isBlank()) {
            nameError = true
            return
        }

        val folder = existingFolder?.copy(
            name = name.trim(),
            description = description.trim()
        ) ?: Folder(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            description = description.trim()
        )

        repository.saveFolder(folder)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Цуцлах", color = TextMuted, fontSize = 14.sp)
                    }
                },
                title = {
                    Text(
                        text = if (folderId == null) "Хавтас үүсгэх" else "Хавтас засах",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                actions = {
                    TextButton(onClick = { saveFolder() }) {
                        Text("Хадгалах", color = Color(0xFFFB923C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        GradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Хавтасны нэр *") },
                    placeholder = { Text("Жишээ: Гадаад хэл, Программчлал...") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Хавтасны нэр заавал оруулна", color = Danger) }
                    } else null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFB923C),
                        cursorColor = Color(0xFFFB923C),
                        focusedLabelColor = Color(0xFFFB923C),
                        unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                        errorBorderColor = Danger,
                        errorLabelColor = Danger
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Тайлбар (Заавал биш)") },
                    placeholder = { Text("Хавтасны тухай товч тайлбар...") },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFB923C),
                        cursorColor = Color(0xFFFB923C),
                        focusedLabelColor = Color(0xFFFB923C),
                        unfocusedBorderColor = TextMuted.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}
