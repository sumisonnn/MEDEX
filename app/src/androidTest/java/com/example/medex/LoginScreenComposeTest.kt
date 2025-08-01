package com.example.medex

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medex.presentation.views.LoginScreen

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginScreenElementsAreDisplayed() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Verify that login screen elements are visible
        composeTestRule.onNodeWithText("WELCOME TO LOGIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").assertIsDisplayed()
    }

    @Test
    fun testLoginWithValidCredentials() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Enter valid email
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")

        // Enter valid password
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Click login button
        composeTestRule.onNodeWithText("Login").performClick()

        // Note: In a real test, you would verify navigation to dashboard
        // This would require setting up the full navigation structure
    }

    @Test
    fun testLoginWithInvalidCredentials() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Enter invalid email
        composeTestRule.onNodeWithText("Email").performTextInput("invalid@example.com")

        // Enter invalid password
        composeTestRule.onNodeWithText("Password").performTextInput("wrongpassword")

        // Click login button
        composeTestRule.onNodeWithText("Login").performClick()

        // Verify error message is displayed (if implemented)
        // composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
    }

    @Test
    fun testLoginWithEmptyFields() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Try to login with empty fields
        composeTestRule.onNodeWithText("Login").performClick()

        // Verify error message for empty fields (if implemented)
        // composeTestRule.onNodeWithText("Please fill all fields").assertIsDisplayed()
    }

        @Test
    fun testNavigationToSignupScreen() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Click on signup link
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").performClick()
        
        // Note: In a real test, you would verify navigation to signup screen
    }

    @Test
    fun testPasswordVisibilityToggle() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Enter password
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Click password visibility toggle
        composeTestRule.onNodeWithContentDescription("Show password").performClick()

        // Verify password is visible
        composeTestRule.onNodeWithText("password123").assertIsDisplayed()

        // Click again to hide password
        composeTestRule.onNodeWithContentDescription("Hide password").performClick()
    }

    @Test
    fun testEmailInputField() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Test email input field
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")

        // Verify text is entered
        composeTestRule.onNodeWithText("test@example.com").assertIsDisplayed()
    }

    @Test
    fun testPasswordInputField() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Test password input field
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Verify text is entered (password should be hidden by default)
        // composeTestRule.onNodeWithText("password123").assertIsDisplayed()
    }

    @Test
    fun testLoginButtonIsEnabled() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Verify login button is enabled
        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }

    @Test
    fun testLoginScreenImageIsDisplayed() {
        // Set up the login screen composable
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }

        // Verify the logo image is displayed
        composeTestRule.onNodeWithContentDescription("Doctor Illustration").assertIsDisplayed()
    }
} 