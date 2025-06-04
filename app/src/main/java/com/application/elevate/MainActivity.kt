package com.application.elevate

import android.annotation.SuppressLint
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
<<<<<<< Updated upstream
import androidx.navigation.compose.rememberNavController
import com.application.elevate.ui.login.LoginPage
import com.application.elevate.ui.register.SignUpPage
import com.application.elevate.ui.splashScreen.SplashScreen
=======
import androidx.navigation.navArgument
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourseDetails
import com.application.elevate.ui.assessment.AssessmentCompletedScreen
import com.application.elevate.ui.assessment.AssessmentScreen
import com.application.elevate.viewmodel.assessment.AssessmentViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.application.elevate.ui.home.HomeScreen

import com.application.elevate.ui.category.CategoryCoursesScreen
import com.application.elevate.ui.category.CategoryScreen
import com.application.elevate.ui.counseling.CounselingScreen
import com.application.elevate.viewmodel.counseling.CounselingViewModel
import com.application.elevate.ui.cvreview.CVReviewResultScreen
import com.application.elevate.ui.cvreview.CVReviewScreen
import com.application.elevate.ui.cvreview.CVReviewListScreen
import com.application.elevate.ui.cvreview.CVReviewDetailScreen
import com.application.elevate.viewmodel.home.HomeViewModel
import com.application.elevate.ui.auth.LoginPage
import com.application.elevate.ui.mycourse.CourseDetailScreen
import com.application.elevate.ui.profile.EditProfileScreen
import com.application.elevate.ui.profile.ProfileScreen
import com.application.elevate.viewmodel.profile.ProfileViewModel
import com.application.elevate.ui.mycourse.CourseScreen
import com.application.elevate.ui.auth.SignUpPage
import com.application.elevate.ui.cvreview.CVReviewViewModel
import com.application.elevate.viewmodel.cvreview.CVReviewListViewModel
import com.application.elevate.viewmodel.cvreview.CVReviewDetailViewModel
import com.application.elevate.ui.home.SearchScreen
import com.application.elevate.ui.splashScreen.SplashScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import com.application.elevate.ui.counseling.CounselingDetailScreen
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
=======
@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalAnimationApi::class)
>>>>>>> Stashed changes
@Preview(showBackground = true)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash_screen" // LoginPage akan tampil pertama kali
    ) {
        composable("login_page") { LoginPage(navController) }
        composable("signup_page") { SignUpPage(navController) }
        composable("splash_screen") { SplashScreen(navController) }
<<<<<<< Updated upstream
=======
        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(navController,viewModel = viewModel) }
        composable("cv_review") {
            val viewModel: CVReviewViewModel = hiltViewModel()
            CVReviewScreen(navController, viewModel)
        }
        composable("search") { SearchScreen(onBackClick = { navController.popBackStack() }) }
        composable("cv_result_review") {
            val viewModel: CVReviewViewModel = hiltViewModel()
            CVReviewResultScreen(navController, viewModel)
        }
        
        // New CV Review routes
        composable("cv_review_list") {
            val viewModel: CVReviewListViewModel = hiltViewModel()
            CVReviewListScreen(navController, viewModel)
        }
        
        composable(
            route = "cv_review_detail/{reviewId}",
            arguments = listOf(navArgument("reviewId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId") ?: ""
            val viewModel: CVReviewDetailViewModel = hiltViewModel()
            CVReviewDetailScreen(navController, reviewId, viewModel)
        }
        
        composable("consultant") {
            val viewModel: CounselingViewModel = hiltViewModel()
            CounselingScreen(viewModel = viewModel, navController = navController)
        }

        composable("assessment") {
            val viewModel: AssessmentViewModel = hiltViewModel()
            AssessmentScreen(viewModel = viewModel, navController = navController)
        }

        composable("assessment_done") {
            AssessmentCompletedScreen(
                onExploreClicked = {
                    navController.navigate("home") {
                        popUpTo("assessment") { inclusive = true }
                    }
                }
            )
        }
        composable("course") { CourseScreen(navController) }

        composable("categories") {
            CategoryScreen(navController = navController)
        }
        
        composable(
            route = "category_courses/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) {
            val categoryId = it.arguments?.getString("categoryId") ?: ""
            CategoryCoursesScreen(
                categoryId = categoryId,
                navController = navController
            )
        }

        composable(
            route = "counseling_detail/{counselorId}",
            arguments = listOf(navArgument("counselorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val counselorId = backStackEntry.arguments?.getInt("counselorId") ?: 0
            val viewModel: CounselingViewModel = hiltViewModel()
            CounselingDetailScreen(navController, counselorId, viewModel)
        }

//        composable("roadmap") {
//            val viewModel: RoadmapViewModel = hiltViewModel()
//            val profileViewModel: ProfileViewModel = hiltViewModel()
//            val uiState by viewModel.uiState.collectAsState()
//            val userData = profileViewModel.getUserData() ?: User(
//                id = 0,
//                firstName = "Guest",
//                lastName = "User",
//                role = "user"
//            )
//            RoadmapScreen(
//                uiState = uiState,
//                user = userData,
//                navController = navController,
//                onCourseClick = { course ->
//                    navController.navigate("course_detail/${course.id}")
//                }
//            )
//        }
>>>>>>> Stashed changes
    }
}