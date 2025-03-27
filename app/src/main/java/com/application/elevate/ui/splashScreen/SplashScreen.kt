package com.application.elevate.ui.splashScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.application.elevate.R
import com.application.elevate.ui.login.poppinsFontFamily
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SplashScreen(navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 0) // Initial page
    val pageCount = 3
    val coroutineScope =
        rememberCoroutineScope() // Menggunakan Coroutine Scope untuk suspend function
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, bottom = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        // HorizontalPager for Swipeable Pages
        HorizontalPager(
            count = pageCount,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 29.dp), // Hanya padding konten halaman
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (page) {
                    0 -> SplashPageOne { coroutineScope.launch { pagerState.animateScrollToPage(page + 1) } }
                    1 -> SplashPageTwo { coroutineScope.launch { pagerState.animateScrollToPage(page + 1) } }
                    2 -> SplashPageThree {}
                }
            }
        }

        // Footer Pagination Dots dan Tombol Next
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 29.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            PaginationDots(currentPage = pagerState.currentPage, totalPages = 3)
            Spacer(modifier = Modifier.weight(1f))

            if (pagerState.currentPage < 3) {
                NextButton {
                    coroutineScope.launch {
                        if (pagerState.currentPage == 2) {
                            // Jika di halaman ketiga, arahkan ke login_page
                            navController.navigate("login_page")
                        } else {
                            // Jika belum di halaman ketiga, geser ke halaman berikutnya
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            }
        }
    }
}


// Pagination Dots Composable
@Composable
fun PaginationDots(currentPage: Int, totalPages: Int) {
    Row(horizontalArrangement = Arrangement.Center) {
        repeat(totalPages) { index ->
            val color = if (index == currentPage) MaterialTheme.colorScheme.primary else Color.Gray
            val width = if (index == currentPage) 48.dp else 16.dp
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .width(width)
                    .padding(4.dp)
                    .background(color, shape = CircleShape)

            )
        }
    }
}

// Tombol Next dengan Ikon Panah

@Composable
fun NextButton(onNextClick: () -> Unit) {
    IconButton(
        onClick = onNextClick,
        modifier = Modifier
            .width(46.dp)
            .height(46.dp)
            .padding(0.dp)
            .clip(CircleShape),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary, // Background warna tombol
            contentColor = MaterialTheme.colorScheme.background // Warna icon
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next",
            tint = Color.White,
            modifier = Modifier.size(19.dp)
        )
    }
}

    @Composable
    fun SplashPageOne(onNextClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Welcome to\n")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("Elevate")
                    }
                    append(" unlock your full\npotential and take your\ncareer to the next level.")
                },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    fontFamily = poppinsFontFamily,
                    lineHeight = 20.dp.value.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(54.dp))


            Image(
                painter = painterResource(id = R.drawable.splashscreen_heroimage_1),
                contentDescription = "Hero Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

    @Composable
    fun SplashPageTwo(onNextClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("Master")
                    }
                    append(
                        "\nin-demand skills and \n" +
                                "stay ahead in your \n" +
                                "career industries"
                    )
                },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    fontFamily = poppinsFontFamily
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(54.dp))


            Image(
                painter = painterResource(id = R.drawable.splashscreen_heroimage_2),
                contentDescription = "Hero Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

    @Composable
    fun SplashPageThree(onNextClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = buildAnnotatedString {
                    append(
                        "The path to \n" +
                                "growth, learning, and \n" +
                                "endless"
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(" possibilities")
                    }
                    append("\nstarts now!")
                },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    fontFamily = poppinsFontFamily
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(54.dp))


            Image(
                painter = painterResource(id = R.drawable.splashscreen_heroimage_3),
                contentDescription = "Hero Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

