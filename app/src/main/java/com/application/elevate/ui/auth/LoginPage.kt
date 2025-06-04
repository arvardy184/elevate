package com.application.elevate.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.elevate.R
import com.application.elevate.ui.theme.PoppinsFontFamily
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.DisposableEffect
import com.application.elevate.viewmodel.auth.LoginViewModel
import com.application.elevate.viewmodel.auth.NavigationEvent

//@Preview(showBackground = true)
//@Composable
//fun LoginPagePreview() {
//    ReplyTheme {
//        // Buat mock ViewModel untuk preview
//        val mockViewModel = object : LoginViewModel(
//            repository = null,
//            userRepository = null
//        ) {
//            override val uiState: StateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
//            override val navigationEvent: StateFlow<NavigationEvent?> = MutableStateFlow(null)
//
//            override fun login(email: String, password: String, rememberMe: Boolean) {
//                // Mock implementation
//            }
//
//            override fun onNavigationHandled() {
//                // Mock implementation
//            }
//        }
//
//        LoginPage(
//            navController = rememberNavController(),
//            viewModel = mockViewModel
//        )
//    }
//}

@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    
    val loginState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val context = LocalContext.current
    
    // Cleanup resources when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            email = ""
            password = ""
            passwordVisible = false
            isEmailFocused = false
            isPasswordFocused = false
            rememberMe = false
        }
    }
    
    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is NavigationEvent.NavigateToHome -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            is NavigationEvent.NavigateToAssessment -> {
                navController.navigate("assessment") {
                    popUpTo("login") { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            null -> {}
        }
    }
    
    // Handle login state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginUiState -> {
                if (loginState.isSuccess) {
                    Toast.makeText(context, loginState.message ?: "Login berhasil", Toast.LENGTH_SHORT).show()
                } else if (loginState.error != null) {
                    Toast.makeText(context, loginState.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 55.dp, start = 29.dp, end = 29.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.Top
    ) {


        Spacer(modifier = Modifier.height(65.dp))

        Text(
            text = "Hello There!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 9.dp)

        )

        Text(
            text = "Glad to see you back",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 35.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp,  modifier = Modifier.padding(top = 2.dp, bottom = 0.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
//                    .shadow(
//                        elevation = if (isEmailFocused or email.isNotEmpty()) 0.dp else 4.dp,
//                        clip = true, // Hilangkan shadow saat fokus
//                        shape = RoundedCornerShape(15.dp)
//                    )
                .onFocusChanged { focusState ->
                    isEmailFocused = focusState.isFocused  // Update status fokus
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,  // Agar background tetap transparan
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Border warna fokus
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Black,
                unfocusedIndicatorColor = if (email.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000),  // Border saat tidak fokus

            ),
            textStyle = LocalTextStyle.current,
            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),




        )

        Spacer(modifier = Modifier.height(9.dp))

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp,  modifier = Modifier.padding(top = 2.dp, bottom = 0.dp)) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = if (passwordVisible) "Hide password" else "Show password", tint = MaterialTheme.colorScheme.primary)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
//                .shadow(
//                    elevation = if (isPasswordFocused or password.isNotEmpty()) 0.dp else 4.dp,
//                    shape = RoundedCornerShape(15.dp),
//                )
                .onFocusChanged { focusState ->
                    isPasswordFocused = focusState.isFocused
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,  // Agar background tetap transparan
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Border warna fokus
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Black,
                unfocusedIndicatorColor = if (password.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000)
            ),
            textStyle = LocalTextStyle.current,
            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(9.dp))

        // Login Button
        Button(
            onClick = { viewModel.login(email, password, rememberMe) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(15.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp),
            enabled = !loginState.isLoading
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = PoppinsFontFamily,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

       Spacer(modifier = Modifier.height(28.dp))

        // Tampilkan pesan error jika ada
        loginState.error?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Remember me dengan Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    modifier = Modifier.size(24.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(text = "Remember me", fontFamily = PoppinsFontFamily, fontSize = 11.dp.value.sp, modifier = Modifier.padding(start = 5.dp))
            }

            Text(
                text = "Forgot Password?",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = PoppinsFontFamily,
                fontSize = 11.dp.value.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.clickable { /* Handle forgot password navigation */ }
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "- Or sign up with -",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { /* Handle Google login */ },
                modifier = Modifier
                    .shadow(
                        shape = RoundedCornerShape(50.dp), elevation = 8.dp, spotColor = Color(0x26000000)



                    )
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(50.dp))
                    .size(56.dp)

            ) {
                Icon(
                    painterResource(id = R.drawable.devicon_apple),
                    contentDescription = "Google",
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { /* Handle Facebook login */ },
                modifier = Modifier
                    .shadow(
                        shape = RoundedCornerShape(50.dp), elevation = 8.dp, spotColor = Color(0x26000000)



                    )
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(50.dp))
                    .size(56.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.devicon_google),
                    contentDescription = "Facebook",
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { /* Handle Twitter login */ },
                modifier = Modifier
                    .shadow(
                        shape = RoundedCornerShape(50.dp), elevation = 8.dp, spotColor = Color(0x26000000)



                    )
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(50.dp))
                    .size(56.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.devicon_facebook),
                    contentDescription = "Twitter",
                    tint = Color.Unspecified
                )
            }
        }





        Spacer(modifier = Modifier.weight(1f))

        // Clickable Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have an account? ", fontFamily = PoppinsFontFamily, fontSize = 11.dp.value.sp)
            Text(
                text = "Register Now",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = PoppinsFontFamily,
                fontSize = 11.dp.value.sp,
                modifier = Modifier.clickable { navController.navigate("signup_page")  }
            )
        }
    }
}
