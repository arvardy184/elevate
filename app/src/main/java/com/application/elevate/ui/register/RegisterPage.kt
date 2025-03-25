package com.application.elevate.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.elevate.R
import com.application.elevate.ui.login.poppinsFontFamily

@Preview(showBackground = true)
@Composable
fun SignUpPage(onBackClick: () -> Unit = {}) {
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isEmailFocused by remember { mutableStateOf(false) }
    var isPhoneFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 55.dp, start = 29.dp, end = 29.dp, bottom = 40.dp ),
        verticalArrangement = Arrangement.Top
    ) {

        IconButton(
            onClick = { onBackClick() }, // Fungsi kembali
            modifier = Modifier.align(Alignment.Start).padding(0.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.padding(end = 16.dp)
            )
        }




        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "Get Started!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(bottom = 9.dp)

        )

        Text(
            text = "Create your Account",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(bottom = 35.dp)
        )


        // Username TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", fontFamily = poppinsFontFamily) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.background)
                .shadow(
                    elevation = if (isEmailFocused) 0.dp else 3.dp,  // Hilangkan shadow saat fokus
                    shape = RoundedCornerShape(15.dp),
                )
                .onFocusChanged { focusState ->
                    isEmailFocused = focusState.isFocused  // Update status fokus
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,  // Agar background tetap transparan
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Border warna fokus
                unfocusedIndicatorColor = Color.Transparent,  // Border saat tidak fokus
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Black
            ),

            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

        )

        Spacer(modifier = Modifier.height(9.dp))

        // Email TextField
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number", fontFamily = poppinsFontFamily) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.background)
                .shadow(
                    elevation = if (isPhoneFocused) 0.dp else 3.dp,  // Hilangkan shadow saat fokus
                    shape = RoundedCornerShape(15.dp),
                )
                .onFocusChanged { focusState ->
                    isPhoneFocused = focusState.isFocused  // Update status fokus
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,  // Agar background tetap transparan
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Border warna fokus
                unfocusedIndicatorColor = Color.Transparent,  // Border saat tidak fokus
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Black
            ),

            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

        )

        Spacer(modifier = Modifier.height(9.dp))

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", fontFamily = poppinsFontFamily) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = if (passwordVisible) "Hide password" else "Show password", tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.background)
                .shadow(
                    elevation = if (isPasswordFocused) 0.dp else 3.dp,  // Hilangkan shadow saat fokus
                    shape = RoundedCornerShape(15.dp),
                )
                .onFocusChanged { focusState ->
                    isPasswordFocused = focusState.isFocused  // Update status fokus
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,  // Agar background tetap transparan
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Border warna fokus
                unfocusedIndicatorColor = Color.Transparent,  // Border saat tidak fokus
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Black
            ),

            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

        )

        Spacer(modifier = Modifier.height(9.dp))

        // Re-enter Password TextField
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password", fontFamily = poppinsFontFamily) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = "Toggle Password Visibility", tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.background)
                .shadow(
                    elevation = if (isConfirmPasswordFocused) 0.dp else 3.dp,  // Hilangkan shadow saat fokus
                    shape = RoundedCornerShape(15.dp),
                )
                .onFocusChanged { focusState ->
                    isConfirmPasswordFocused = focusState.isFocused  // Update status fokus
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,  // Agar background tetap transparan
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Border warna fokus
                unfocusedIndicatorColor = Color.Transparent,  // Border saat tidak fokus
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Black
            ),

            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

        )

        Spacer(modifier = Modifier.height(28.dp))

        // Submit Button
        Button(
            onClick = { /* Handle sign-up logic */ },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Background color
                contentColor = MaterialTheme.colorScheme.background // Text color
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 4.dp,  // Sesuai dengan offset Y = 64 dari Figma
                    shape = RoundedCornerShape(15.dp),
                    spotColor = Color(0x01000000)  // Warna hitam transparan (0% opacity)
                )
                .shadow(
                    elevation = 2.dp,  //
                    shape = RoundedCornerShape(15.dp),
                    spotColor = Color(0x03000000)
                )
                .shadow(
                    elevation = 3.dp,  //
                    spotColor = Color(0x04000000)
                )
        ) {
            Text(text = "Sign Up", fontFamily = poppinsFontFamily, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "- Or sign up with -",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = poppinsFontFamily,
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
        )  {
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
                    painterResource(id = R.drawable.devicon_apple), // Replace with actual Google icon
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
                    painterResource(id = R.drawable.devicon_google), // Replace with actual Facebook icon
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
                    painterResource(id = R.drawable.devicon_facebook), // Replace with actual Twitter icon
                    contentDescription = "Twitter",
                    tint = Color.Unspecified
                )
            }
        }




        Spacer(modifier = Modifier.weight(1f))

        // Clickable Text (Navigate to Login Page)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", fontFamily = poppinsFontFamily, fontSize = 11.dp.value.sp)
            Text(
                text = "Login Now",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = poppinsFontFamily,
                fontSize = 11.dp.value.sp,
                modifier = Modifier.clickable { /* Navigate to Login screen */ }
            )
        }
    }
}
