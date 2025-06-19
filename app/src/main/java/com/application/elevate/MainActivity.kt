package com.application.elevate

import android.annotation.SuppressLint
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.elevate.ui.splashScreen.SplashScreen
import androidx.navigation.navArgument
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourseDetails
import com.application.elevate.ui.assessment.AssessmentCompletedScreen
import com.application.elevate.ui.assessment.AssessmentScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.application.elevate.ui.home.HomeScreen

import com.application.elevate.ui.category.CategoryCoursesScreen
import com.application.elevate.ui.category.CategoryScreen
import com.application.elevate.ui.counseling.CounselingScreen
import com.application.elevate.ui.cvreview.CVReviewResultScreen
import com.application.elevate.ui.cvreview.CVReviewScreen
import com.application.elevate.ui.cvreview.CVReviewListScreen
import com.application.elevate.ui.cvreview.CVReviewDetailScreen
import com.application.elevate.ui.auth.LoginPage
import com.application.elevate.ui.mycourse.CourseDetailScreen
import com.application.elevate.ui.profile.EditProfileScreen
import com.application.elevate.ui.profile.ProfileScreen
import com.application.elevate.ui.mycourse.CourseScreen
import com.application.elevate.ui.auth.SignUpPage
import com.application.elevate.viewmodel.cvreview.CVReviewListViewModel
import com.application.elevate.viewmodel.cvreview.CVReviewDetailViewModel
import com.application.elevate.ui.search.AdvancedSearchScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.application.elevate.ui.counseling.CounselingDetailScreen
import com.application.elevate.viewmodel.assessment.AssessmentViewModel
import com.application.elevate.viewmodel.counseling.CounselingViewModel
import com.application.elevate.viewmodel.home.HomeViewModel
import com.application.elevate.ui.auth.LoginPage
import com.application.elevate.ui.cvreview.CVReviewViewModel
import com.application.elevate.ui.mycourse.CourseDetailScreen
import com.application.elevate.ui.profile.EditProfileScreen
import com.application.elevate.ui.profile.ProfileScreen
import com.application.elevate.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hilangkan action bar
        // WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            MaterialTheme {
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

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalAnimationApi::class)

@Preview(showBackground = true)
@Composable
fun AppNavigation() {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = "splash_screen",
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
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) {
            val courseId = it.arguments?.getInt("courseId") ?: 1
            val courseDetail = dummyCourseDetails.find { it.id == courseId.toString() }!!
            CourseDetailScreen(courseDetail = courseDetail, onBackClick = { navController.popBackStack() })
        }

        composable("login_page") {
            LoginPage(navController = navController)
        }

        composable("signup_page") {
            SignUpPage(navController = navController)
        }

        composable("splash_screen") { SplashScreen(navController) }

        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(navController,viewModel = viewModel) }
        composable("cv_review") {
            val viewModel: CVReviewViewModel = hiltViewModel()
            CVReviewScreen(navController, viewModel)
        }
        composable("search") { 
            AdvancedSearchScreen(
                onBackClick = { navController.popBackStack() },
                onCourseClick = { course -> 
                    navController.navigate("course_detail/${course.id}")
                },
                onConsultantClick = { consultant ->
                    navController.navigate("counseling_detail/${consultant.id}")
                }
            ) 
        }
        composable("advanced_search") { 
            AdvancedSearchScreen(
                onBackClick = { navController.popBackStack() },
                onCourseClick = { course -> 
                    navController.navigate("course_detail/${course.id}")
                },
                onConsultantClick = { consultant ->
                    navController.navigate("counseling_detail/${consultant.id}")
                }
            ) 
        }
        composable("cv_result_review") {
            val parentEntry = remember(it) {
                navController.getBackStackEntry("cv_review")
            }
            val viewModel: CVReviewViewModel = hiltViewModel(parentEntry)
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
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) {
            val categoryId = it.arguments?.getInt("categoryId") ?: 1
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

    }
}