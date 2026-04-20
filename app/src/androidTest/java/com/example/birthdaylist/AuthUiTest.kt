package com.example.birthdaylist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.birthdaylist.ui.view.LoginScreen
import org.junit.Rule
import org.junit.Test

/**
 * En forbedret UI-test der bruger testTags for at undgå dubletter.
 */
class AuthUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_shows_correct_title() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Vi leder nu efter det unikke tag "login_title"
        composeTestRule.onNodeWithTag("login_title").assertExists()
    }

    @Test
    fun loginScreen_can_type_email_and_password() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Brug tags i stedet for tekst for at være helt præcis
        composeTestRule.onNodeWithTag("email_field").performTextInput("test@test.com")
        composeTestRule.onNodeWithTag("password_field").performTextInput("123456")

        // Tjek om knappen findes via dens tag "login_button"
        composeTestRule.onNodeWithTag("login_button").assertExists()
    }
}
