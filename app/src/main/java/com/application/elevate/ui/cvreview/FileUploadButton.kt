package com.application.elevate.ui.cvreview


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.application.elevate.R

@Composable
fun FileUploadButton(onFileUploaded: () -> Unit) {
    Button(
        onClick = { onFileUploaded() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF65558F),  // Warna latar belakang tombol
            contentColor = Color.White           // Menetapkan warna teks tombol
        ),
        modifier = Modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("Upload CV")
    }
}

