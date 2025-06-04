package com.application.elevate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.model.Consultant

@Composable
fun ConsultantCard(
  consultant: Consultant, 
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {}
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(4.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White)
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Avatar placeholder
      Box(
        modifier = Modifier
          .size(60.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = consultant.users.firstName.first().toString() + 
                 consultant.users.lastName.first().toString(),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(
        modifier = Modifier.weight(1f)
      ) {
        // Name dengan verified badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = consultant.users.fullName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )
          if (consultant.verified) {
            Icon(
              Icons.Filled.Verified,
              contentDescription = "Verified",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
        
        // Specialization
        Text(
          text = consultant.specialization.replace("-", " ").split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating dan session count
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Rating
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Rating",
              tint = Color(0xFFFFD700),
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (consultant.averageRating > 0) 
                String.format("%.1f", consultant.averageRating)
              else "No rating",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 12.sp
            )
          }
          
          // Sessions count
          Text(
            text = "${consultant.totalSessions} sessions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Bio preview
        Text(
          text = consultant.bio,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      // View details button
      CustomButton(
        text = "View Details",
        onClick = onClick,
        fontSize = 10.sp,
        modifier = Modifier.align(Alignment.CenterVertically)
      )
    }
  }
}