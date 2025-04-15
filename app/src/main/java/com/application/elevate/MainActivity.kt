package com.application.elevate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.elevate.ui.cvreview.CVReviewResultScreen
import com.application.elevate.ui.cvreview.CVReviewScreen
import com.application.elevate.ui.home.HomeScreen

import com.application.elevate.ui.counseling.CategoryScreen
import com.application.elevate.ui.counseling.CounselingScreen
import com.application.elevate.ui.counseling.CounselingViewModel
import com.application.elevate.ui.home.HomeScreen
import com.application.elevate.ui.home.HomeViewModel

import com.application.elevate.ui.login.LoginPage
import com.application.elevate.ui.mycourse.CourseScreen
import com.application.elevate.ui.profile.EditProfileScreen
import com.application.elevate.ui.profile.ProfileScreen
import com.application.elevate.ui.profile.ProfileViewModel
import com.application.elevate.ui.register.SignUpPage
import com.application.elevate.ui.splashScreen.SplashScreen
import com.application.elevate.ui.theme.ReplyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReplyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,

        startDestination = "splash_screen"
        startDestination = "home" // LoginPage akan tampil pertama kali

    ) {
        composable("profile") {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(viewModel = viewModel, navController = navController)
        }

        composable("edit_profile") {
            val viewModel = ProfileViewModel() // atau hiltViewModel() jika pakai DI
            EditProfileScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("counseling") { CounselingScreen(viewModel = CounselingViewModel(), navController = NavController(context = androidx.compose.ui.platform.LocalContext.current )) }
        composable("login_page") { LoginPage(navController) }
        composable("signup_page") { SignUpPage(navController) }
        composable("splash_screen") { SplashScreen(navController) }
        composable("home_screen") { HomeScreen(navController) }
        composable("cv_review"){ CVReviewScreen(navController)}
        composable("cv_result_review"){ CVReviewResultScreen(navController)}
        composable("consultant") { CounselingScreen(viewModel = CounselingViewModel(),navController ) }
        composable("home") { HomeScreen(viewModel = HomeViewModel(), navController)}
        composable("course_screen") { CourseScreen(navController)}

    }

}

