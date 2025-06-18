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
import com.application.elevate.ui.assessment.MyAssessmentScreen
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
import com.application.elevate.ui.auth.SignUpPage
import com.application.elevate.ui.mycourse.CourseDetailScreen
import com.application.elevate.ui.profile.EditProfileScreen
import com.application.elevate.ui.profile.ProfileScreen
import com.application.elevate.ui.mycourse.CourseScreen
import com.application.elevate.viewmodel.cvreview.CVReviewListViewModel
import com.application.elevate.viewmodel.cvreview.CVReviewDetailViewModel
import com.application.elevate.ui.home.SearchScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.application.elevate.ui.counseling.CounselingDetailScreen
import com.application.elevate.viewmodel.assessment.AssessmentViewModel
import com.application.elevate.viewmodel.counseling.CounselingViewModel
import com.application.elevate.viewmodel.home.HomeViewModel
import com.application.elevate.ui.jobmatching.JobMatchingScreen
import com.application.elevate.ui.jobmatching.JobMatchingResultScreen
import com.application.elevate.viewmodel.cvreview.CVReviewViewModel
import com.application.elevate.viewmodel.jobmatching.JobMatchingViewModel
import com.application.elevate.viewmodel.profile.ProfileViewModel
import com.application.elevate.viewmodel.course.CourseViewModel
import com.application.elevate.util.SyncManager
import com.application.elevate.worker.CourseSyncWorker
import com.application.elevate.util.NetworkMonitor
import com.application.elevate.util.NotificationManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var syncManager: SyncManager
    
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    
    @Inject
    lateinit var notificationManager: NotificationManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize sync on app startup
        initializeSync()
        
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
    
    private fun initializeSync() {
        // Schedule periodic sync and trigger immediate sync if online
        syncManager.schedulePeriodicSync()
        syncManager.triggerImmediateSync()
        
        // Schedule course sync worker
        CourseSyncWorker.enqueue(this)
        
        // Monitor network changes and trigger sync when online
        lifecycleScope.launch {
            networkMonitor.isOnline().collect { isOnline ->
                if (isOnline) {
                    // Trigger immediate sync when device goes online
                    syncManager.triggerImmediateSync()
                    CourseSyncWorker.enqueue(this@MainActivity)
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
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseIdString = backStackEntry.arguments?.getString("courseId") ?: "0"
            val courseId = courseIdString.toIntOrNull() ?: 0
            CourseDetailScreen(
                courseId = courseId,
                onBackClick = { navController.popBackStack() }
            )
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
        composable("search") { SearchScreen(onBackClick = { navController.popBackStack() }) }
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
        
        composable("my_assessment") {
            MyAssessmentScreen(navController = navController)
        }
        
        composable("course") { 
            val viewModel: CourseViewModel = hiltViewModel()
            com.application.elevate.ui.mycourse.CourseScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

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

        composable("job_skill_matching") {
            val viewModel: JobMatchingViewModel = hiltViewModel()
            JobMatchingScreen(navController, viewModel)
        }
        
        composable("job_matching_result") {
            val parentEntry = remember(it) {
                navController.getBackStackEntry("job_skill_matching")
            }
            val viewModel: JobMatchingViewModel = hiltViewModel(parentEntry)
            JobMatchingResultScreen(navController, viewModel)
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