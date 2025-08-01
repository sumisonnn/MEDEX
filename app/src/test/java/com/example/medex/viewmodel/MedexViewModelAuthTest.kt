package com.example.medex.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MedexViewModelAuthTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockMedicinesRef: DatabaseReference

    @Mock
    private lateinit var mockSalesRef: DatabaseReference

    @Mock
    private lateinit var mockUserRef: DatabaseReference

    @Mock
    private lateinit var mockRoleRef: DatabaseReference

    @Mock
    private lateinit var mockProfileRef: DatabaseReference

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Mock
    private lateinit var mockAuthTask: Task<AuthResult>

    @Mock
    private lateinit var mockDataSnapshot: DataSnapshot

    @Mock
    private lateinit var mockDatabaseError: DatabaseError

    @Mock
    private lateinit var mockDatabaseTask: Task<DataSnapshot>
    
    @Mock
    private lateinit var mockVoidTask: Task<Void>

    private lateinit var viewModel: MedexViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Setup mocks
        `when`(mockDatabase.getReference("medicines")).thenReturn(mockMedicinesRef)
        `when`(mockDatabase.getReference("sales")).thenReturn(mockSalesRef)
        `when`(mockDatabase.getReference("users")).thenReturn(mockUserRef)
        
        // Create ViewModel with mocked dependencies
        viewModel = MedexViewModel(mockAuth, mockDatabase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test successful login updates state correctly`() {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val uid = "user123"
        
        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockAuthTask)
        `when`(mockAuthTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.email).thenReturn(email)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockUserRef.child(uid)).thenReturn(mockRoleRef)
        `when`(mockRoleRef.child("role")).thenReturn(mockRoleRef)
        `when`(mockRoleRef.get()).thenReturn(mockDatabaseTask)
        `when`(mockDatabaseTask.isSuccessful).thenReturn(true)
        `when`(mockDatabaseTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnSuccessListener<DataSnapshot>>(0)
            listener.onSuccess(mockDataSnapshot)
            mockDatabaseTask
        }
        `when`(mockAuthTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockAuthTask)
            mockAuthTask
        }

        var callbackResult = false
        val callback = { result: Boolean -> callbackResult = result }

        // When
        viewModel.login(email, password, callback)

        // Then
        assertFalse(viewModel.isLoading)
        assertEquals(email, viewModel.currentUsername)
        assertNull(viewModel.authError)
        assertTrue(callbackResult)
    }

    @Test
    fun `test failed login sets error correctly`() {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val errorMessage = "Invalid credentials"
        
        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockAuthTask)
        `when`(mockAuthTask.isSuccessful).thenReturn(false)
        `when`(mockAuthTask.exception).thenReturn(Exception(errorMessage))
        `when`(mockAuthTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockAuthTask)
            mockAuthTask
        }

        var callbackResult = false
        val callback = { result: Boolean -> callbackResult = result }

        // When
        viewModel.login(email, password, callback)

        // Then
        assertFalse(viewModel.isLoading)
        assertEquals(errorMessage, viewModel.authError)
        assertFalse(callbackResult)
    }

    @Test
    fun `test successful signup creates user and sets role`() {
        // Given
        val email = "newuser@example.com"
        val password = "password123"
        val uid = "newuser123"
        
        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockAuthTask)
        `when`(mockAuthTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.email).thenReturn(email)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockUserRef.child(uid)).thenReturn(mockRoleRef)
        `when`(mockRoleRef.child("role")).thenReturn(mockRoleRef)
        `when`(mockAuthTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockAuthTask)
            mockAuthTask
        }

        var callbackResult = false
        val callback = { result: Boolean -> callbackResult = result }

        // When
        viewModel.signup(email, password, callback)

        // Then
        assertFalse(viewModel.isLoading)
        assertEquals(email, viewModel.currentUsername)
        assertNull(viewModel.authError)
        assertEquals("user", viewModel.userRole)
        assertTrue(callbackResult)
        verify(mockRoleRef).setValue("user")
    }

    @Test
    fun `test failed signup sets error correctly`() {
        // Given
        val email = "existing@example.com"
        val password = "password123"
        val errorMessage = "Email already in use"
        
        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockAuthTask)
        `when`(mockAuthTask.isSuccessful).thenReturn(false)
        `when`(mockAuthTask.exception).thenReturn(Exception(errorMessage))
        `when`(mockAuthTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockAuthTask)
            mockAuthTask
        }

        var callbackResult = false
        val callback = { result: Boolean -> callbackResult = result }

        // When
        viewModel.signup(email, password, callback)

        // Then
        assertFalse(viewModel.isLoading)
        assertEquals(errorMessage, viewModel.authError)
        assertFalse(callbackResult)
    }

    @Test
    fun `test loadUserProfile loads user data correctly`() {
        // Given
        val uid = "user123"
        val name = "John Doe"
        val email = "john@example.com"
        val phone = "1234567890"
        
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockUserRef.child(uid)).thenReturn(mockProfileRef)
        `when`(mockProfileRef.child("profile")).thenReturn(mockProfileRef)
        `when`(mockProfileRef.get()).thenReturn(mockDatabaseTask)
        `when`(mockDatabaseTask.isSuccessful).thenReturn(true)
        `when`(mockDatabaseTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnSuccessListener<DataSnapshot>>(0)
            listener.onSuccess(mockDataSnapshot)
            mockDatabaseTask
        }
        
        // Mock DataSnapshot for profile data
        `when`(mockDataSnapshot.child("name")).thenReturn(mockDataSnapshot)
        `when`(mockDataSnapshot.child("email")).thenReturn(mockDataSnapshot)
        `when`(mockDataSnapshot.child("phone")).thenReturn(mockDataSnapshot)
        `when`(mockDataSnapshot.getValue(String::class.java)).thenReturn(name, email, phone)

        // When
        viewModel.loadUserProfile()

        // Then
        assertEquals(name, viewModel.profileName)
        assertEquals(email, viewModel.profileEmail)
        assertEquals(phone, viewModel.profilePhone)
    }

    @Test
    fun `test updateUserProfile updates database and state`() {
        // Given
        val uid = "user123"
        val name = "Jane Doe"
        val email = "jane@example.com"
        val phone = "0987654321"
        
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockUserRef.child(uid)).thenReturn(mockProfileRef)
        `when`(mockProfileRef.child("profile")).thenReturn(mockProfileRef)
        `when`(mockProfileRef.setValue(any())).thenReturn(mockVoidTask)
        `when`(mockVoidTask.isSuccessful).thenReturn(true)
        `when`(mockVoidTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnSuccessListener<Void>>(0)
            listener.onSuccess(null)
            mockVoidTask
        }

        var callbackResult = false
        val callback = { result: Boolean -> callbackResult = result }

        // When
        viewModel.updateUserProfile(name, email, phone, callback)

        // Then
        assertEquals(name, viewModel.profileName)
        assertEquals(email, viewModel.profileEmail)
        assertEquals(phone, viewModel.profilePhone)
        assertTrue(callbackResult)
        verify(mockProfileRef).setValue(mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone
        ))
    }

    @Test
    fun `test updateUserProfile failure calls callback with false`() {
        // Given
        val uid = "user123"
        val name = "Jane Doe"
        val email = "jane@example.com"
        val phone = "0987654321"
        
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockUserRef.child(uid)).thenReturn(mockProfileRef)
        `when`(mockProfileRef.child("profile")).thenReturn(mockProfileRef)
        `when`(mockProfileRef.setValue(any())).thenReturn(mockVoidTask)
        `when`(mockVoidTask.isSuccessful).thenReturn(false)
        `when`(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask)
        `when`(mockVoidTask.addOnFailureListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnFailureListener>(0)
            listener.onFailure(Exception("Database error"))
            mockVoidTask
        }

        var callbackResult = true
        val callback = { result: Boolean -> callbackResult = result }

        // When
        viewModel.updateUserProfile(name, email, phone, callback)

        // Then
        assertFalse(callbackResult)
    }

    @Test
    fun `test loadUserProfile does nothing when user is not logged in`() {
        // Given
        `when`(mockAuth.currentUser).thenReturn(null)

        // When
        viewModel.loadUserProfile()

        // Then
        assertEquals("", viewModel.profileName)
        assertEquals("", viewModel.profileEmail)
        assertEquals("", viewModel.profilePhone)
        verify(mockUserRef, never()).child(any())
    }

    @Test
    fun `test updateUserProfile does nothing when user is not logged in`() {
        // Given
        `when`(mockAuth.currentUser).thenReturn(null)

        var callbackCalled = false
        val callback = { result: Boolean -> callbackCalled = true }

        // When
        viewModel.updateUserProfile("name", "email", "phone", callback)

        // Then
        assertFalse(callbackCalled)
        verify(mockUserRef, never()).child(any())
    }
} 