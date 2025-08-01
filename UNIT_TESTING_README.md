# Unit Testing Setup for MEDEX Android App

This document outlines the unit testing implementation for the MEDEX Android application using Mockito and JUnit.

## Overview

Unit testing is a fundamental aspect of software testing where individual components or functions of a software application are tested in isolation. This method ensures that each unit of the software performs as expected, helping identify and fix bugs early in the development process.

## Dependencies Added

The following dependencies have been added to `app/build.gradle.kts`:

```kotlin
testImplementation("org.mockito:mockito-core:5.6.0")
testImplementation("org.mockito:mockito-inline:5.2.0")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("androidx.arch.core:core-testing:2.2.0")
```

## Mockito Framework

Mockito is a Java-based mocking framework used in conjunction with JUnit and TestNG. It internally uses Java Reflection API and allows creating objects of a service. A mock object returns dummy data and avoids external dependencies.

### Key Features:
- **Mocking**: Create mock objects for external dependencies
- **Stubbing**: Define behavior for mock objects
- **Verification**: Verify that methods were called with expected parameters
- **Argument Matchers**: Use flexible argument matching

## Test Structure

### 1. ViewModel Tests (`MedexViewModelTest.kt`)

Tests for the main ViewModel functionality including:
- Cart operations (add, remove, clear)
- Medicine management (CRUD operations)
- Sales operations
- Authentication state management
- Database interactions

### 2. Authentication Tests (`MedexViewModelAuthTest.kt`)

Specific tests for authentication functionality:
- Login success/failure scenarios
- Signup success/failure scenarios
- User profile loading and updating
- Error handling

### 3. Data Model Tests

#### Medicine Tests (`MedicineTest.kt`)
- Default values
- Property assignments
- Copy functionality
- Equality and hashCode
- toString representation
- Component functions

#### Sale Tests (`SaleTest.kt`)
- Default values
- Property assignments
- Copy functionality
- Equality and hashCode
- toString representation
- Component functions

## Key Testing Concepts

### 1. Dependency Injection for Testability

The `MedexViewModel` has been refactored to accept dependencies as constructor parameters:

```kotlin
class MedexViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : ViewModel()
```

This allows us to inject mock objects during testing while maintaining backward compatibility.

### 2. Mocking External Dependencies

```kotlin
@Mock
private lateinit var mockAuth: FirebaseAuth

@Mock
private lateinit var mockDatabase: FirebaseDatabase

@Mock
private lateinit var mockFirebaseUser: FirebaseUser
```

### 3. Test Structure (Given-When-Then)

```kotlin
@Test
fun `test addToCart adds medicine to cart`() {
    // Given
    val medicine = Medicine(id = "1", name = "Test Medicine", price = 10.0)

    // When
    viewModel.addToCart(medicine)

    // Then
    assertEquals(1, viewModel.cart.size)
    assertTrue(viewModel.cart.contains(medicine))
}
```

### 4. Async Testing

For testing asynchronous operations, we use coroutines testing:

```kotlin
@ExperimentalCoroutinesApi
class MedexViewModelTest {
    private val testDispatcher = TestCoroutineDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
```

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests "com.example.medex.viewmodel.MedexViewModelTest"
```

### Run Specific Test Method
```bash
./gradlew test --tests "com.example.medex.viewmodel.MedexViewModelTest.test addToCart adds medicine to cart"
```

## Test Coverage

The current test suite covers:

### ViewModel Tests (15 tests)
- ✅ Cart operations (add, remove, clear)
- ✅ Medicine retrieval and filtering
- ✅ Sales filtering
- ✅ Authentication state
- ✅ Database operations
- ✅ Error scenarios

### Authentication Tests (8 tests)
- ✅ Successful login
- ✅ Failed login
- ✅ Successful signup
- ✅ Failed signup
- ✅ User profile loading
- ✅ User profile updating
- ✅ Error handling

### Data Model Tests (12 tests each)
- ✅ Default values
- ✅ Property assignments
- ✅ Copy functionality
- ✅ Equality and hashCode
- ✅ toString representation
- ✅ Component functions

## Best Practices

### 1. Test Naming
Use descriptive test names that explain the scenario:
```kotlin
@Test
fun `test recordSale with insufficient stock does not create sale`()
```

### 2. Mock Verification
Verify that mock methods are called with expected parameters:
```kotlin
verify(mockAuth).signOut()
verify(mockMedicinesRef).child("1")
```

### 3. Argument Matchers
Use flexible argument matching:
```kotlin
verify(mockSalesRef).child(any())
```

### 4. Exception Testing
Test both success and failure scenarios:
```kotlin
@Test
fun `test failed login sets error correctly`()
```

## Benefits of This Testing Setup

1. **Early Bug Detection**: Issues are caught during development
2. **Refactoring Safety**: Changes can be made with confidence
3. **Documentation**: Tests serve as living documentation
4. **Design Improvement**: Forces better separation of concerns
5. **Regression Prevention**: Prevents old bugs from returning

## Future Enhancements

1. **Integration Tests**: Test with real Firebase emulator
2. **UI Tests**: Test Compose UI components
3. **Performance Tests**: Test with large datasets
4. **Coverage Reports**: Generate detailed coverage reports
5. **Continuous Integration**: Automate test running

## Troubleshooting

### Common Issues

1. **Mockito Version Conflicts**: Ensure all Mockito dependencies use compatible versions
2. **Coroutine Testing**: Use `@ExperimentalCoroutinesApi` for coroutine tests
3. **Firebase Mocks**: Mock Firebase tasks and listeners properly
4. **Test Isolation**: Each test should be independent

### Debugging Tests

Add logging to understand test behavior:
```kotlin
println("Debug: Cart size is ${viewModel.cart.size}")
```

## Conclusion

This unit testing setup provides a solid foundation for maintaining code quality and reliability. The tests cover the core functionality of the MEDEX app and can be extended as new features are added.

Remember to:
- Run tests frequently during development
- Add tests for new features
- Refactor tests when code changes
- Keep tests simple and focused
- Use meaningful test names and descriptions 