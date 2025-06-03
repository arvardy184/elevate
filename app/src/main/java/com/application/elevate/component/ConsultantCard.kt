package com.application.elevate.ui.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.R
import com.application.elevate.model.Consultant
import com.application.elevate.ui.theme.ReplyTheme

@Composable
fun ConsultantCard(consultant: Consultant, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),

    ) {
        Row(modifier = Modifier.background(Color.White).padding(16.dp)) {
            Image(
                painter = painterResource(id = consultant.imageResId),
                contentDescription = consultant.name,
                modifier = Modifier
                    .width(75.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(consultant.name, style = MaterialTheme.typography.titleMedium)
                Text(consultant.title, style = MaterialTheme.typography.bodySmall)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (it < consultant.rating.toInt()) Color.Yellow else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("(${consultant.reviewCount})", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(35.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Column(modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_discount), // ganti sesuai drawable-mu
                                contentDescription = "Coin Icon",
                                modifier = Modifier
                                    .size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Rp ${consultant.price}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rp ${consultant.oldPrice}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = Color.Gray
                            )
                        )
                    }

                    CustomButton(
                        text = "Get Consultation",
                        onClick = { /* Navigate to detail */ },
                        fontSize = 10.dp.value.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConsultantCardPreview() {
    ReplyTheme  {
        ConsultantCard(consultant = Consultant("1", "Barbie S.Ds., M.Ds.", "UI/UX Design Consultant", 4.0f, 191, "1",25000, 29999, R.drawable.barbie),
        )
    }
}