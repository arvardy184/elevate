@file:Suppress("DEPRECATION")

package com.application.elevate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourseDetails
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.application.elevate.ui.home.HomeScreen

import com.application.elevate.ui.counseling.CategoryScreen
import com.application.elevate.ui.counseling.CounselingScreen
import com.application.elevate.ui.counseling.CounselingViewModel
import com.application.elevate.ui.cvreview.CVReviewResultScreen
import com.application.elevate.ui.cvreview.CVReviewScreen
import com.application.elevate.ui.home.HomeScreen
import com.application.elevate.ui.home.HomeViewModel

import com.application.elevate.ui.login.LoginPage
import com.application.elevate.ui.mycourse.CourseDetailScreen
import com.application.elevate.ui.profile.EditProfileScreen
import com.application.elevate.ui.profile.ProfileScreen
import com.application.elevate.ui.profile.ProfileViewModel
import com.application.elevate.ui.mycourse.CourseScreen
import com.application.elevate.ui.register.SignUpPage
import com.application.elevate.ui.search.SearchScreen
import com.application.elevate.ui.splashScreen.SplashScreen
import com.application.elevate.ui.theme.ReplyTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReplyTheme {
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

@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun AppNavigation() {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) }
    ) {
        composable("profile") {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(viewModel = viewModel, navController = navController)
        }

        composable("edit_profile") {
            val viewModel: ProfileViewModel = hiltViewModel()
            EditProfileScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = "course_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) {
            val courseId = it.arguments?.getString("courseId") ?: ""
            val courseDetail = dummyCourseDetails.find { it.id == courseId }!!
            CourseDetailScreen(courseDetail = courseDetail, onBackClick = { navController.popBackStack() })
        }

        composable("login_page") { LoginPage(navController) }
        composable("signup_page") { SignUpPage(navController) }
        composable("splash_screen") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("cv_review") { CVReviewScreen(navController) }
        composable("search") { SearchScreen(onBackClick = { navController.popBackStack() }) }
        composable("cv_result_review") { CVReviewResultScreen(navController) }
        composable("consultant") {
            val viewModel: CounselingViewModel = hiltViewModel()
            CounselingScreen(viewModel = viewModel, navController = navController)
        }
        composable("course") { CourseScreen(navController) }
    }
}