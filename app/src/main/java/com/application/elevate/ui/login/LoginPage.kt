package com.application.elevate.ui.login

import android.content.res.Resources.Theme
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.elevate.R
import com.application.elevate.ui.dropShadow
import com.application.elevate.ui.theme.ReplyTheme


val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val poppinsFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Poppins"),
        fontProvider = provider
    )
)

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    // Gunakan theme aplikasi agar preview sesuai dengan style sebenarnya
    ReplyTheme {
        // Buat dummy NavController untuk keperluan preview
        val navController = rememberNavController()
        LoginPage(navController = navController)
    }
}


@Composable
fun LoginPage( navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 55.dp, start = 29.dp, end = 29.dp, bottom = 40.dp ),

        verticalArrangement = Arrangement.Top
    ) {


        Spacer(modifier = Modifier.height(65.dp))

        Text(
            text = "Hello There!",
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
            text = "Glad to see you back",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(bottom = 35.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", fontFamily = poppinsFontFamily, fontSize = 13.dp.value.sp,  modifier = Modifier.padding(top = 2.dp, bottom = 0.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .shadow(
                    elevation = if (isEmailFocused or email.isNotEmpty()) 0.dp else 4.dp,  // Hilangkan shadow saat fokus
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
                unfocusedIndicatorColor = if (email.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Transparent,  // Border saat tidak fokus
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
            label = { Text("Password", fontFamily = poppinsFontFamily, fontSize = 13.dp.value.sp,  modifier = Modifier.padding(top = 2.dp, bottom = 0.dp)) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = if (passwordVisible) "Hide password" else "Show password", tint = MaterialTheme.colorScheme.primary)
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,  // Agar background tetap transparan
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Border warna fokus
                unfocusedIndicatorColor = if (password.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Transparent,  // Border saat tidak fokus
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Black
            ),
            shape = RoundedCornerShape(15.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .shadow(
                    elevation = if (isPasswordFocused or password.isNotEmpty()) 0.dp else 4.dp,  // Hilangkan shadow saat fokus
                    shape = RoundedCornerShape(15.dp),
                )
                .onFocusChanged { focusState ->
                    isPasswordFocused = focusState.isFocused  // Update status fokus
                },
        )

       Spacer(modifier = Modifier.height(28.dp))

        // Submit Button
        Button(
            onClick = {
                      navController.navigate("home_screen")
                      },
            shape = RoundedCornerShape(15.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Background color
                contentColor = MaterialTheme.colorScheme.background // Text color
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
                    elevation = 2.dp,  //
                    shape = RoundedCornerShape(15.dp),
                    spotColor = Color(0x03000000)
                )
                .shadow(
                    elevation = 3.dp,  //
                    spotColor = Color(0x04000000)
                )

        ) {
            Text(text = "Login", fontFamily = poppinsFontFamily, fontSize = 16.dp.value.sp)
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
                var isChecked by remember { mutableStateOf(false) }
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    modifier = Modifier.size(24.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(text = "Remember me", fontFamily = poppinsFontFamily, fontSize = 11.dp.value.sp, modifier = Modifier.padding(start = 5.dp))
            }


            Text(
                text = "Forgot Password?",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = poppinsFontFamily,
                fontSize = 11.dp.value.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.clickable { /* Handle forgot password navigation */ }
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

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
            Text(text = "Don't have an account? ", fontFamily = poppinsFontFamily, fontSize = 11.dp.value.sp)
            Text(
                text = "Register Now",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = poppinsFontFamily,
                fontSize = 11.dp.value.sp,
                modifier = Modifier.clickable { navController.navigate("signup_page")  }
            )
        }
    }
}
