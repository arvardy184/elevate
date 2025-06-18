package com.application.elevate.ui.counseling

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.elevate.R
import com.application.elevate.viewmodel.counseling.CounselingViewModel
import com.application.elevate.model.Consultant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
            "Counselor Details",
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
            "Book Consultation",
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
          CounselorDetailContent(
            consultant = uiState.consultant!!
          )
        }
      }
    }
  }
}

@Composable
fun CounselorDetailContent(consultant: Consultant) {
  // State variables untuk show more functionality
  var isAboutExpanded by remember { mutableStateOf(false) }
  var isExperienceExpanded by remember { mutableStateOf(false) }
  var isReviewsExpanded by remember { mutableStateOf(false) }
  
  // State untuk session preferences
  var selectedSessionType by remember { mutableStateOf("Video Call") }
  var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
  var enableReminders by remember { mutableStateOf(true) }
  var receiveNotifications by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Profile Header
    ProfileHeaderSection(consultant = consultant)
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // About Section dengan Show More
    AboutSection(
      consultant = consultant,
      isExpanded = isAboutExpanded,
      onToggleExpanded = { isAboutExpanded = !isAboutExpanded }
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Experience Section dengan Show More
    ExperienceSection(
      isExpanded = isExperienceExpanded,
      onToggleExpanded = { isExperienceExpanded = !isExperienceExpanded }
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Reviews Section dengan Show More
    ReviewsSection(
      isExpanded = isReviewsExpanded,
      onToggleExpanded = { isReviewsExpanded = !isReviewsExpanded }
    )
 
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Session Preferences Section
    SessionPreferencesSection(
      selectedSessionType = selectedSessionType,
      onSessionTypeSelected = { selectedSessionType = it },
      enableReminders = enableReminders,
      onReminderToggle = { enableReminders = it },
      receiveNotifications = receiveNotifications,
      onNotificationToggle = { receiveNotifications = it }
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Payment Breakdown
    PaymentBreakdown()
    
    Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom button
  }
}

@Composable
fun ProfileHeaderSection(consultant: Consultant) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Profile Image
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.barbie),
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
      Row(verticalAlignment = Alignment.CenterVertically) {
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
          else "4.8",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "(${consultant.totalSessions} sessions)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun AboutSection(
  consultant: Consultant,
  isExpanded: Boolean,
  onToggleExpanded: () -> Unit
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "About ${consultant.users.fullName}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      TextButton(onClick = onToggleExpanded) {
        Text(
          text = if (isExpanded) "Show Less" else "Show More",
          style = MaterialTheme.typography.bodySmall
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
      }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Text(
      text = consultant.bio,
      style = MaterialTheme.typography.bodyMedium,
      lineHeight = 20.sp,
      maxLines = if (isExpanded) Int.MAX_VALUE else 3,
      overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
    )
    
    if (isExpanded) {
      Spacer(modifier = Modifier.height(16.dp))
      
      // Specializations
      Text(
        text = "Specializations:",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(4.dp))
      
      val specializations = listOf(
        "Career Guidance", "Life Coaching", "Stress Management", 
        "Goal Setting", "Work-Life Balance"
      )
      
      specializations.forEach { specialization ->
        Row(modifier = Modifier.padding(vertical = 2.dp)) {
          Text(
            text = "• $specialization",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
fun ExperienceSection(
  isExpanded: Boolean,
  onToggleExpanded: () -> Unit
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Experience",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      TextButton(onClick = onToggleExpanded) {
        Text(
          text = if (isExpanded) "Show Less" else "Show More",
          style = MaterialTheme.typography.bodySmall
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
      }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Show only first experience if collapsed
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
    
    if (isExpanded) {
      Spacer(modifier = Modifier.height(16.dp))
      
      ExperienceItem(
        company = "Canva",
        position = "UX Strategy Specialist",
        duration = "Jul 2019 – Dec 2020",
        achievements = listOf(
          "Optimized the mobile editor UI, increasing editing speed and efficiency by 25% for frequent users.",
          "Introduced a new feedback loop system that improved iteration time by 3x across design teams."
        )
      )
      
      Spacer(modifier = Modifier.height(16.dp))
      
      ExperienceItem(
        company = "Adobe",
        position = "Junior UX Designer",
        duration = "Mar 2018 – Jun 2019",
        achievements = listOf(
          "Designed user flows for Creative Cloud mobile applications.",
          "Conducted user research and usability testing for key features."
        )
      )
    }
  }
}

@Composable
fun ReviewsSection(
  isExpanded: Boolean,
  onToggleExpanded: () -> Unit
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Reviews & Testimonials",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      TextButton(onClick = onToggleExpanded) {
        Text(
          text = if (isExpanded) "Show Less" else "Show More",
          style = MaterialTheme.typography.bodySmall
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
      }
    }
    
    if (isExpanded) {
      Spacer(modifier = Modifier.height(12.dp))
      
      // Review items
      ReviewItem(
        reviewerName = "Sarah M.",
        rating = 5,
        reviewText = "Sangat membantu dalam memberikan guidance untuk career switch saya. Highly recommended!",
        date = "2 minggu lalu"
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      ReviewItem(
        reviewerName = "Ahmad R.",
        rating = 5,
        reviewText = "Professional dan sangat understanding. Sesi konsultasinya benar-benar worth it.",
        date = "1 bulan lalu"
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      ReviewItem(
        reviewerName = "Dina K.",
        rating = 4,
        reviewText = "Good insights dan practical advice. Akan booking lagi untuk follow-up session.",
        date = "2 bulan lalu"
      )
    } else {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "4.8/5 from 127 reviews",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun AvailabilitySection(
  selectedDate: LocalDate?,
  onDateSelected: (LocalDate?) -> Unit
) {
  Column {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.CalendarToday,
        contentDescription = "Calendar",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
   
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Date selection dengan DatePicker (simplified version)
    OutlinedTextField(
      value = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "",
      onValueChange = { },
      readOnly = true,
      placeholder = { Text("Pilih tanggal konsultasi") },
      trailingIcon = {
        Icon(
          imageVector = Icons.Default.CalendarToday,
          contentDescription = "Select Date"
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .clickable {
          // TODO: Show DatePickerDialog
          onDateSelected(LocalDate.now().plusDays(1))
        }
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Available time slots
    Text(
      text = "Available Time Slots:",
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    val timeSlots = listOf("09:00", "11:00", "14:00", "16:00", "19:00")
    
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      timeSlots.take(3).forEach { time ->
        FilterChip(
          onClick = { },
          label = { Text(time) },
          selected = false,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
fun SessionPreferencesSection(
  selectedSessionType: String,
  onSessionTypeSelected: (String) -> Unit,
  enableReminders: Boolean,
  onReminderToggle: (Boolean) -> Unit,
  receiveNotifications: Boolean,
  onNotificationToggle: (Boolean) -> Unit
) {
  Column {
    Text(
      text = "Session Preferences",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Session Type Selection dengan RadioButton
    Text(
      text = "Session Type:",
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    val sessionTypes = listOf("Video Call", "Voice Call", "Chat Only")
    
    sessionTypes.forEach { type ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSessionTypeSelected(type) }
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        RadioButton(
          selected = selectedSessionType == type,
          onClick = { onSessionTypeSelected(type) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = type,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
    
  
    
    // Notifications checkbox
  
  }
}

@Composable
fun ReviewItem(
  reviewerName: String,
  rating: Int,
  reviewText: String,
  date: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = reviewerName,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = date,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      
      Spacer(modifier = Modifier.height(4.dp))
      
      // Rating stars
      Row {
        repeat(5) { index ->
          Icon(
            Icons.Filled.Star,
            contentDescription = "Star",
            tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
            modifier = Modifier.size(14.dp)
          )
        }
      }
      
      Spacer(modifier = Modifier.height(8.dp))
      
      Text(
        text = reviewText,
        style = MaterialTheme.typography.bodySmall,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
fun ExperienceItem(
  company: String,
  position: String,
  duration: String,
  achievements: List<String>
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
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
      
      Spacer(modifier = Modifier.height(8.dp))
      
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
}

@Composable
fun PaymentBreakdown() {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        text = "Payment Details",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      
      Spacer(modifier = Modifier.height(16.dp))
      
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
          text = "Payment Total",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Rp 26,000",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
} 