package com.application.elevate.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.elevate.R
import com.application.elevate.ui.home.HomeScreen
import com.application.elevate.ui.theme.ReplyTheme


//fun GrowthHubItem(label: String, imageRes: Int) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(),
//        modifier = Modifier
//            .width(IntrinsicSize.Max)
//            .height(IntrinsicSize.Min)
//            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
//    ) {
//        Row(
//            modifier = Modifier.padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = imageRes),
//                contentDescription = label,
//                modifier = Modifier.size(42.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(label)
//        }
//    }
//}
//

@Composable
fun GrowthHubItem(
    label: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
            .background(color = MaterialTheme.colorScheme.background)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)  // Menambahkan kemampuan klik
    ) {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ReplyTheme { // Pastikan ini adalah theme kamu
        GrowthHubItem(label = "hello", imageRes = R.drawable.counseling ) {
            
        }
    }
}