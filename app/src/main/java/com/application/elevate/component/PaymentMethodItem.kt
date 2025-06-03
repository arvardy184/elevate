package com.application.elevate.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.application.elevate.model.PaymentMethod

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    selected: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(method.id) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selected,
            onClick = { onSelect(method.id) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = method.name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}