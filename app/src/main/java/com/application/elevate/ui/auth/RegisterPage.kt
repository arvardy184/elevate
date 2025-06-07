package com.application.elevate.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LocalTextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R
import com.application.elevate.viewmodel.auth.AuthViewModel
import com.application.elevate.viewmodel.auth.NavigationEvent
import com.application.elevate.ui.theme.PoppinsFontFamily
import com.application.elevate.ui.theme.ReplyTheme
import com.google.relay.compose.ColumnScopeInstanceImpl.weight

@Preview(showBackground = true)
@Composable
fun RegisterPagePreview() {
    ReplyTheme {
        val navController = rememberNavController()
        // Untuk preview, gunakan SignUpPage tanpa viewModel
        SignUpPagePreview(navController)
    }
}

// Versi composable SignUpPage khusus untuk preview (tanpa ViewModel)
@Composable
fun SignUpPagePreview(navController: NavController) {
    // Logic SignUpPage sama tapi tanpa ViewModel
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPhoneFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }
    var isFirstNameFocused by remember { mutableStateOf(false) }
    var isLastNameFocused by remember { mutableStateOf(false) }
    
    RegisterContent(
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        email = email,
        password = password,
        rePassword = rePassword,
        passwordVisible = passwordVisible,
        confirmPasswordVisible = confirmPasswordVisible,
        isEmailFocused = isEmailFocused,
        isPhoneFocused = isPhoneFocused,
        isPasswordFocused = isPasswordFocused,
        isConfirmPasswordFocused = isConfirmPasswordFocused,
        isFirstNameFocused = isFirstNameFocused,
        isLastNameFocused = isLastNameFocused,
        isLoading = false,
        errorMessage = null,
        onFirstNameChange = { firstName = it },
        onLastNameChange = { lastName = it },
        onPhoneNumberChange = { phoneNumber = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onRePasswordChange = { rePassword = it },
        onPasswordVisibilityChange = { passwordVisible = it },
        onConfirmPasswordVisibilityChange = { confirmPasswordVisible = it },
        onFirstNameFocusChange = { isFirstNameFocused = it },
        onLastNameFocusChange = { isLastNameFocused = it },
        onEmailFocusChange = { isEmailFocused = it },
        onPhoneFocusChange = { isPhoneFocused = it },
        onPasswordFocusChange = { isPasswordFocused = it },
        onConfirmPasswordFocusChange = { isConfirmPasswordFocused = it },
        onRegisterClick = { },
        onLoginClick = { }
    )
}

@Composable
fun SignUpPage(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPhoneFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }
    var isFirstNameFocused by remember { mutableStateOf(false) }
    var isLastNameFocused by remember { mutableStateOf(false) }
    
    val viewModel: AuthViewModel = hiltViewModel()
    val authState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val context = LocalContext.current
    
    // Side effect untuk menangani registrasi berhasil
    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            // Tampilkan toast sukses
            Toast.makeText(context, authState.message ?: "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Side effect untuk menangani navigasi
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is NavigationEvent.NavigateToLogin -> {
                navController.navigate("login_page") {
                    popUpTo("signup_page") { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            else -> {} // Menangani semua kasus lainnya
        }
    }
    
    RegisterContent(
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        email = email,
        password = password,
        rePassword = rePassword,
        passwordVisible = passwordVisible,
        confirmPasswordVisible = confirmPasswordVisible,
        isEmailFocused = isEmailFocused,
        isPhoneFocused = isPhoneFocused,
        isPasswordFocused = isPasswordFocused,
        isConfirmPasswordFocused = isConfirmPasswordFocused,
        isFirstNameFocused = isFirstNameFocused,
        isLastNameFocused = isLastNameFocused,
        isLoading = authState.isLoading,
        errorMessage = if (!authState.isSuccess) authState.message else null,
        onFirstNameChange = { firstName = it },
        onLastNameChange = { lastName = it },
        onPhoneNumberChange = { phoneNumber = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onRePasswordChange = { rePassword = it },
        onPasswordVisibilityChange = { passwordVisible = it },
        onConfirmPasswordVisibilityChange = { confirmPasswordVisible = it },
        onFirstNameFocusChange = { isFirstNameFocused = it },
        onLastNameFocusChange = { isLastNameFocused = it },
        onEmailFocusChange = { isEmailFocused = it },
        onPhoneFocusChange = { isPhoneFocused = it },
        onPasswordFocusChange = { isPasswordFocused = it },
        onConfirmPasswordFocusChange = { isConfirmPasswordFocused = it },
        onRegisterClick = { 
            viewModel.register(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                rePassword = rePassword,
                phoneNumber = phoneNumber
            )
        },
        onLoginClick = { navController.navigate("login_page") }
    )
}

@Composable
fun RegisterContent(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    email: String,
    password: String,
    rePassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    isEmailFocused: Boolean,
    isPhoneFocused: Boolean,
    isPasswordFocused: Boolean,
    isConfirmPasswordFocused: Boolean,
    isFirstNameFocused: Boolean,
    isLastNameFocused: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRePasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit,
    onFirstNameFocusChange: (Boolean) -> Unit,
    onLastNameFocusChange: (Boolean) -> Unit,
    onEmailFocusChange: (Boolean) -> Unit,
    onPhoneFocusChange: (Boolean) -> Unit,
    onPasswordFocusChange: (Boolean) -> Unit,
    onConfirmPasswordFocusChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var showErrorDialog by remember { mutableStateOf(false) }
    
    // Update showErrorDialog ketika ada error message
    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }
    
    // Tampilkan dialog error jika showErrorDialog true
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error", fontFamily = PoppinsFontFamily) },
            text = { Text(errorMessage ?: "", fontFamily = PoppinsFontFamily) },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK", fontFamily = PoppinsFontFamily)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 0.dp, start = 29.dp, end = 29.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "Get Started!",
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
                text = "Create your Account",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 35.dp)
            )
        }

        // First Name TextField
        item {
            OutlinedTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                label = { Text("First Name", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .onFocusChanged { focusState ->
                        onFirstNameFocusChange(focusState.isFocused)
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (firstName.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Black
                ),
                textStyle = LocalTextStyle.current,
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(9.dp))
        }

        // Last Name TextField
        item {
            OutlinedTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = { Text("Last Name", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .onFocusChanged { focusState ->
                        onLastNameFocusChange(focusState.isFocused)
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (lastName.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Black
                ),
                textStyle = LocalTextStyle.current,
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(9.dp))
        }

        // Email TextField
        item {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)

                    .onFocusChanged { focusState ->
                        onEmailFocusChange(focusState.isFocused)
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (email.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Black
                ),
                textStyle = LocalTextStyle.current,
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(9.dp))
        }

        // Phone Number TextField
        item {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Phone Number", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .onFocusChanged { focusState ->
                        onPhoneFocusChange(focusState.isFocused)
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (phoneNumber.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Black
                ),
                textStyle = LocalTextStyle.current,
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(9.dp))
        }

        // Password TextField
        item {
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                        Icon(image, contentDescription = if (passwordVisible) "Hide password" else "Show password", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (password.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Black
                ),
                textStyle = LocalTextStyle.current,
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)

                    .onFocusChanged { focusState ->
                        onPasswordFocusChange(focusState.isFocused)
                    }
            )

            Spacer(modifier = Modifier.height(9.dp))
        }

        // Re-enter Password TextField
        item {
            OutlinedTextField(
                value = rePassword,
                onValueChange = onRePasswordChange,
                label = { Text("Re-enter Password", fontFamily = PoppinsFontFamily, fontSize = 13.dp.value.sp) },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { onConfirmPasswordVisibilityChange(!confirmPasswordVisible) }) {
                        Icon(image, contentDescription = "Toggle Password Visibility", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .onFocusChanged { focusState ->
                        onConfirmPasswordFocusChange(focusState.isFocused)
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (rePassword.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0x40000000),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Black
                ),
                textStyle = LocalTextStyle.current,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Submit Button
        item {
            Button(
                onClick = onRegisterClick,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(15.dp),
                        spotColor = Color(0x01000000)
                    )
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(15.dp),
                        spotColor = Color(0x03000000)
                    )
                    .shadow(
                        elevation = 3.dp,
                        spotColor = Color(0x04000000)
                    )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.background,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Register", fontFamily = PoppinsFontFamily, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Social Login Section
        item {
            Text(
                text = "- Or sign up with -",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Social Login Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { /* Handle Apple login */ },
                    modifier = Modifier
                        .shadow(
                            shape = RoundedCornerShape(50.dp), elevation = 8.dp, spotColor = Color(0x26000000)
                        )
                        .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(50.dp))
                        .size(56.dp)
                ) {
                    Icon(
                        painterResource(id = R.drawable.devicon_apple),
                        contentDescription = "Apple",
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

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
                        painterResource(id = R.drawable.devicon_google),
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
                        painterResource(id = R.drawable.devicon_facebook),
                        contentDescription = "Facebook",
                        tint = Color.Unspecified
                    )
                }
            }
        }

        // Login Link
        item {
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ", 
                    fontFamily = PoppinsFontFamily, 
                    fontSize = 11.dp.value.sp
                )
                Text(
                    text = "Login Now",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 11.dp.value.sp,
                    modifier = Modifier.clickable(onClick = onLoginClick)
                )
            }
        }
    }
}

