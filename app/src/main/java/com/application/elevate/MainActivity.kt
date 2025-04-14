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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.elevate.ui.counseling.CategoryScreen
import com.application.elevate.ui.counseling.CounselingScreen
import com.application.elevate.ui.counseling.CounselingViewModel
import com.application.elevate.ui.home.HomeScreen
import com.application.elevate.ui.home.HomeViewModel
import com.application.elevate.ui.login.LoginPage
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
        startDestination = "home" // LoginPage akan tampil pertama kali
    ) {
        composable("login_page") { LoginPage(navController) }
        composable("signup_page") { SignUpPage(navController) }
        composable("splash_screen") { SplashScreen(navController) }
        composable("consultant") { CounselingScreen(viewModel = CounselingViewModel(),navController ) }
        composable("home") { HomeScreen(viewModel = HomeViewModel(), navController)}
    }

}