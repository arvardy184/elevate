package com.application.elevate.ui.counseling

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.elevate.R
import com.application.elevate.viewmodel.counseling.CounselingViewModel
import com.application.elevate.model.Consultant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselingDetailScreen(
  navController: NavController,
  counselorId: Int,
  viewModel: CounselingViewModel = hiltViewModel()
) {
  val uiState by viewModel.detailUiState.collectAsState()
  
  LaunchedEffect(counselorId) {
    viewModel.loadCounselorDetail(counselorId)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { 
          Text(
            "Payment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
          )
        },
        navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    bottomBar = {
      // Continue Button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(16.dp)
      ) {
        Button(
          onClick = { 
            // TODO: Navigate to payment processing
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6C5CE7)
          )
        ) {
          Text(
            "Continue",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when {
        uiState.isLoading -> {
          CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
          )
        }
        
        uiState.error != null -> {
          Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = uiState.error ?: "Unknown error",
              color = MaterialTheme.colorScheme.error,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { viewModel.loadCounselorDetail(counselorId) }
            ) {
              Text("Retry")
            }
          }
        }
        
        uiState.consultant != null -> {
          CounselorPaymentContent(
            consultant = uiState.consultant!!
          )
        }
      }
    }
  }
}

@Composable
fun CounselorPaymentContent(consultant: Consultant) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Profile Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Profile Image Placeholder
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        // Untuk demo, pakai drawable. Nanti bisa ganti dengan AsyncImage dari URL
        Image(
          painter = painterResource(id = R.drawable.barbie), // Ganti dengan foto dari API
          contentDescription = consultant.users.fullName,
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
        )
      }
      
      Spacer(modifier = Modifier.width(16.dp))
      
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = consultant.users.fullName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = consultant.specialization.replace("-", " ").split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Rating
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Filled.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (consultant.averageRating > 0) 
              String.format("%.1f", consultant.averageRating)
            else "4.8", // Default untuk demo
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "(${consultant.totalSessions})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // About Section
    Text(
      text = "About ${consultant.users.fullName}",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = consultant.bio,
      style = MaterialTheme.typography.bodyMedium,
      lineHeight = 20.sp
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Experience Section
    Text(
      text = "Experience",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(12.dp))
    
    // Experience Items (Dummy data - replace with real data when available)
    ExperienceItem(
      company = "Figma",
      position = "Senior UI/UX Design Consultant",
      duration = "Jan 2021 – Jun 2023",
      achievements = listOf(
        "Spearheaded the redesign of Figma's onboarding experience, resulting in a 30% increase in user activation rate.",
        "Led cross-functional workshops to integrate accessibility best practices across design components.",
        "Collaborated with the product and dev teams to develop scalable design systems used by 100k+ users."
      )
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    ExperienceItem(
      company = "Canva",
      position = "UX Strategy Specialist",
      duration = "Jul 2021 – Dec 2023",
      achievements = listOf(
        "Optimized the mobile editor UI, increasing editing speed and efficiency by 25% for frequent users.",
        "Introduced a new feedback loop system that improved iteration time by 3x across design teams."
      )
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Payment Breakdown
    PaymentBreakdown()
    
    Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom button
  }
}

@Composable
fun ExperienceItem(
  company: String,
  position: String,
  duration: String,
  achievements: List<String>
) {
  Column {
    Text(
      text = "$company – $position",
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = duration,
      style = MaterialTheme.typography.bodySmall,
      fontStyle = FontStyle.Italic,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(4.dp))
    
    achievements.forEach { achievement ->
      Row(
        modifier = Modifier.padding(vertical = 2.dp)
      ) {
        Text(
          text = "•",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(end = 8.dp)
        )
        Text(
          text = achievement,
          style = MaterialTheme.typography.bodySmall,
          lineHeight = 16.sp,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
fun PaymentBreakdown() {
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    // Consultation Fee
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Consultation Fee (30 mins)",
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = "Rp 29,999",
        style = MaterialTheme.typography.bodyMedium,
        textDecoration = TextDecoration.LineThrough,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Discount Voucher
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Discount Voucher",
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = "-Rp 4,999",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFFE53E3E)
      )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Service Charge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Service Charge",
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = "Rp 1,000",
        style = MaterialTheme.typography.bodyMedium
      )
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Divider
    HorizontalDivider(
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Payment Total
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Payment Totals",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Rp 26,000",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
      )
    }
  }
} 