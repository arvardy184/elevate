package com.application.elevate.ui.counseling

import android.widget.RatingBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.application.elevate.model.Consultant
import com.application.elevate.model.PaymentDetail
import com.application.elevate.model.PaymentMethod
import com.application.elevate.ui.theme.ReplyTheme
import com.application.elevate.R
import com.application.elevate.component.RatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    uiState: CounselingUiState,
    onContinue: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf<String?>(null) }
    val consultant = uiState.selectedConsultant
    val detail = uiState.paymentDetail

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Payment") },
            navigationIcon = {
                IconButton(onClick = { /* TODO: navBack */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        consultant?.let { c ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.counseling),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(c.name, style = MaterialTheme.typography.titleMedium)
                    Text(c.title, style = MaterialTheme.typography.bodySmall);
                    RatingBar(rating = c.rating)
                }
            }
        }

        detail?.let { d ->
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("About ${uiState.selectedConsultant?.name}", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = uiState.selectedConsultant?.about ?: "—",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Divider()
                Spacer(Modifier.height(8.dp))

                // Fee breakdown
                listOf(
                    "Consultation Fee (30 mins)" to "Rp ${d.consultationFee}",
                    "Discount Voucher" to "-Rp ${d.discount}",
                    "Service Charge" to "Rp ${d.serviceCharge}"
                ).forEach { (label, value) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label)
                        if (label == "Service Charge") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(value)
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        } else {
                            Text(
                                value,
                                color = if (label.startsWith("Discount")) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment Totals", style = MaterialTheme.typography.titleMedium)
                    Text("Rp ${d.total}", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(16.dp))
                Text("Payment Methods", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(d.methods) { m ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = m.id }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMethod == m.id,
                                onClick = { selectedMethod = m.id }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(m.name)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaymentScreen() {
    val dummyConsultant = Consultant("1","Barbie S.Ds.","UI/UX Design Consultant",4f,191,25000,R.drawable.counseling)
    val dummyDetail = PaymentDetail(
        consultationFee = 29999,
        discount = 4999,
        serviceCharge = 1000,
        total = 26000,
        methods = listOf(
            PaymentMethod("gopay","Gopay",0),
            PaymentMethod("dana","DANA",0),
            PaymentMethod("shopeepay","ShopeePay",0),
            PaymentMethod("ovo","OVO",0)
        )
    )
    val uiState = CounselingUiState(
        selectedConsultant = dummyConsultant,
        paymentDetail = dummyDetail
    )
    ReplyTheme {
        PaymentScreen(uiState = uiState, onContinue = {})
    }
}
