# Espresso UI Testing for MEDEX Android App

This document outlines the Espresso UI testing implementation for the MEDEX Android application.

## Overview

Espresso is a UI testing framework for Android that allows you to create automated tests for your app's user interface. It works like a robot that finds UI elements, interacts with them, and verifies the expected behavior.

## Key Concepts

### 1. View Matchers
- **Finding Views**: Locate UI elements using matchers like `withId()`, `withText()`, `withClassName()`
- **Hierarchy**: Navigate through view hierarchy to find specific elements
- **Custom Matchers**: Create custom matchers for complex UI elements

### 2. View Actions
- **User Interactions**: Perform actions like `click()`, `typeText()`, `swipe()`
- **Keyboard Actions**: Handle keyboard interactions with `closeSoftKeyboard()`
- **Complex Actions**: Combine multiple actions for complex interactions

### 3. View Assertions
- **State Verification**: Check if views are displayed, enabled, or have specific text
- **Content Validation**: Verify text content, image resources, or other properties
- **Custom Assertions**: Create custom assertions for specific requirements

## Dependencies Added

The following dependencies have been added to `app/build.gradle.kts`:

```kotlin
androidTestImplementation("androidx.test:runner:1.2.0")
androidTestImplementation("androidx.test:rules:1.2.0")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
```

## Test Structure

### 1. Login Screen Tests (`LoginScreenTest.kt`)

Tests for authentication functionality:
- ✅ Login screen elements display
- ✅ Valid credentials login
- ✅ Invalid credentials handling
- ✅ Empty fields validation
- ✅ Navigation to signup
- ✅ Password visibility toggle
- ✅ Remember me functionality
- ✅ Forgot password link

### 2. Dashboard Screen Tests (`DashboardScreenTest.kt`)

Tests for main dashboard functionality:
- ✅ Dashboard elements display
- ✅ Medicine list display
- ✅ Add to cart functionality
- ✅ Medicine search
- ✅ Medicine filtering
- ✅ Navigation to cart/profile
- ✅ Medicine details navigation
- ✅ Logout functionality
- ✅ Bottom navigation
- ✅ Quantity increment/decrement

### 3. Cart Screen Tests (`CartScreenTest.kt`)

Tests for shopping cart functionality:
- ✅ Cart screen elements display
- ✅ Empty cart display
- ✅ Cart item display
- ✅ Quantity increment/decrement
- ✅ Remove item from cart
- ✅ Cart total calculation
- ✅ Checkout button states
- ✅ Checkout process
- ✅ Continue shopping
- ✅ Cart item details
- ✅ Cart persistence

### 4. Checkout Screen Tests (`CheckoutScreenTest.kt`)

Tests for checkout process:
- ✅ Checkout screen elements display
- ✅ Order summary display
- ✅ Shipping address form
- ✅ Payment method selection
- ✅ Credit card form
- ✅ Cash on delivery option
- ✅ Form validation
- ✅ Successful order placement
- ✅ Order confirmation screen
- ✅ Back navigation
- ✅ Order summary calculation
- ✅ Shipping options
- ✅ Promo code application

## Test Implementation Examples

### Basic Test Structure

```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginScreenTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testLoginWithValidCredentials() {
        // 1. Match a View: Find the email input
        onView(withId(R.id.login_email_input))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        
        // 2. Perform an action: Enter password
        onView(withId(R.id.login_password_input))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        // 3. Assert and verify: Click login and check navigation
        onView(withId(R.id.login_button))
            .perform(click())
        
        onView(withId(R.id.dashboard_container))
            .check(matches(isDisplayed()))
    }
}
```

### Common View Matchers

```kotlin
// Find by ID
onView(withId(R.id.login_button))

// Find by text
onView(withText("Login"))

// Find by content description
onView(withContentDescription("Login button"))

// Find by class name
onView(withClassName(Matchers.equalTo(EditText::class.java.name)))

// Find by resource ID
onView(withResourceName("login_button"))
```

### Common View Actions

```kotlin
// Click action
onView(withId(R.id.button)).perform(click())

// Type text
onView(withId(R.id.edit_text)).perform(typeText("Hello"), closeSoftKeyboard())

// Clear text
onView(withId(R.id.edit_text)).perform(clearText())

// Scroll to element
onView(withId(R.id.scroll_view)).perform(scrollTo())

// Swipe action
onView(withId(R.id.swipe_view)).perform(swipeLeft())

// Long click
onView(withId(R.id.button)).perform(longClick())
```

### Common View Assertions

```kotlin
// Check if view is displayed
onView(withId(R.id.button)).check(matches(isDisplayed()))

// Check if view is enabled
onView(withId(R.id.button)).check(matches(isEnabled()))

// Check text content
onView(withId(R.id.text_view)).check(matches(withText("Expected text")))

// Check if view is not displayed
onView(withId(R.id.hidden_view)).check(matches(not(isDisplayed())))

// Check if view is checked (for checkboxes)
onView(withId(R.id.checkbox)).check(matches(isChecked()))
```

## Best Practices

### 1. Test Organization
- Group related tests in the same test class
- Use descriptive test method names
- Follow the Given-When-Then pattern

### 2. Element Identification
- Use stable IDs for UI elements
- Avoid using text that might change
- Use content descriptions for accessibility

### 3. Test Independence
- Each test should be independent
- Don't rely on the state from previous tests
- Use `@Before` and `@After` methods for setup/cleanup

### 4. Performance
- Keep tests focused and fast
- Avoid unnecessary waits
- Use `IdlingResource` for async operations

### 5. Error Handling
- Test both positive and negative scenarios
- Verify error messages and states
- Test edge cases and boundary conditions

## Running Tests

### Run All UI Tests
```bash
./gradlew connectedAndroidTest
```

### Run Specific Test Class
```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.medex.LoginScreenTest
```

### Run Specific Test Method
```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.medex.LoginScreenTest#testLoginWithValidCredentials
```

## Test Coverage

The current Espresso test suite covers:

### Login Screen (8 tests)
- ✅ Screen elements display
- ✅ Valid login flow
- ✅ Invalid login handling
- ✅ Form validation
- ✅ Navigation
- ✅ UI interactions

### Dashboard Screen (12 tests)
- ✅ Dashboard elements
- ✅ Medicine list functionality
- ✅ Cart operations
- ✅ Search and filter
- ✅ Navigation
- ✅ User interactions

### Cart Screen (12 tests)
- ✅ Cart display and management
- ✅ Quantity operations
- ✅ Item removal
- ✅ Total calculations
- ✅ Checkout process
- ✅ Cart persistence

### Checkout Screen (13 tests)
- ✅ Checkout flow
- ✅ Form validation
- ✅ Payment methods
- ✅ Order processing
- ✅ Confirmation
- ✅ Navigation

## Advanced Testing Techniques

### 1. Custom Matchers
```kotlin
fun withDrawable(@DrawableRes id: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("with drawable from resource id: $id")
        }

        override fun matchesSafely(item: View?): Boolean {
            val context = item?.context
            val expectedDrawable = ContextCompat.getDrawable(context!!, id)
            val actualDrawable = (item as ImageView).drawable
            return expectedDrawable?.constantState == actualDrawable?.constantState
        }
    }
}
```

### 2. Idling Resources
```kotlin
class CustomIdlingResource : IdlingResource {
    private var callback: IdlingResource.ResourceCallback? = null
    private var isIdle = false

    override fun getName(): String = "CustomIdlingResource"

    override fun isIdleNow(): Boolean = isIdle

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    fun setIdleState(isIdleNow: Boolean) {
        isIdle = isIdleNow
        if (isIdleNow) {
            callback?.onTransitionToIdle()
        }
    }
}
```

### 3. RecyclerView Testing
```kotlin
@Test
fun testRecyclerViewItem() {
    onView(withId(R.id.recycler_view))
        .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
}
```

## Troubleshooting

### Common Issues

1. **Element Not Found**
   - Check if the element ID exists
   - Verify the element is visible
   - Use `withText()` as fallback

2. **Timing Issues**
   - Use `IdlingResource` for async operations
   - Add appropriate waits
   - Check for loading states

3. **Test Flakiness**
   - Ensure test independence
   - Use stable element identifiers
   - Avoid relying on timing

### Debugging Tips

1. **Screenshot on Failure**
```kotlin
@get:Rule
val screenshotRule = ScreenshotRule()
```

2. **Log Test Steps**
```kotlin
Log.d("EspressoTest", "Performing action: $actionName")
```

3. **Custom Failure Messages**
```kotlin
onView(withId(R.id.button))
    .check(matches(isDisplayed()))
    .withFailureHandler { view, matcher ->
        // Custom failure handling
    }
```

## Future Enhancements

1. **Visual Testing**: Add visual regression testing
2. **Accessibility Testing**: Test accessibility features
3. **Performance Testing**: Measure UI performance
4. **Cross-Device Testing**: Test on multiple devices
5. **Continuous Integration**: Automate test execution

## Conclusion

This Espresso testing setup provides comprehensive UI testing coverage for the MEDEX Android application. The tests cover all major user flows and ensure the app behaves correctly across different scenarios.

The implementation follows Android testing best practices and provides a solid foundation for maintaining UI quality and reliability. 