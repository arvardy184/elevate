package com.application.elevate.ui.cvreview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable

fun PrimaryButton(
  text: String, 
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false
) {
  Button(
    onClick = { onClick() },
    modifier = modifier.then(
      if (modifier == Modifier) Modifier.fillMaxWidth() else Modifier
    ),
    enabled = enabled && !isLoading,
    colors = ButtonDefaults.buttonColors(
      containerColor = Color(0xFF65558F),
      contentColor = Color.White,
      disabledContainerColor = Color(0xFF65558F).copy(alpha = 0.5f),
      disabledContentColor = Color.White.copy(alpha = 0.7f)
    ),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(16.dp),
          color = Color.White,
          strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(text)
    }
  }

}