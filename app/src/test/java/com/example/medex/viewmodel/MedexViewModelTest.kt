package com.example.medex.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medex.data.model.Medicine
import com.example.medex.data.model.Sale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
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
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MedexViewModelTest {

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
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockAuthResult: com.google.firebase.auth.AuthResult

    @Mock
    private lateinit var mockTask: com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult>

    @Mock
    private lateinit var mockDataSnapshot: DataSnapshot

    @Mock
    private lateinit var mockDatabaseError: DatabaseError

    private lateinit var viewModel: MedexViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Setup mocks
        `when`(mockDatabase.getReference("medicines")).thenReturn(mockMedicinesRef)
        `when`(mockDatabase.getReference("sales")).thenReturn(mockSalesRef)
        `when`(mockDatabase.getReference("users")).thenReturn(mockUserRef)
        
        // Mock child operations for database references
        `when`(mockMedicinesRef.child(any())).thenReturn(mockMedicinesRef)
        `when`(mockSalesRef.child(any())).thenReturn(mockSalesRef)
        `when`(mockUserRef.child(any())).thenReturn(mockUserRef)
        
        // Mock setValue operations
        `when`(mockMedicinesRef.setValue(any())).thenReturn(null)
        `when`(mockSalesRef.setValue(any())).thenReturn(null)
        `when`(mockUserRef.setValue(any())).thenReturn(null)
        
        // Mock setValue on child references
        `when`(mockMedicinesRef.child(any()).setValue(any())).thenReturn(null)
        `when`(mockSalesRef.child(any()).setValue(any())).thenReturn(null)
        
        // Mock removeValue operations
        `when`(mockMedicinesRef.removeValue()).thenReturn(null)
        `when`(mockSalesRef.removeValue()).thenReturn(null)
        `when`(mockUserRef.removeValue()).thenReturn(null)
        
        // Mock addValueEventListener operations
        `when`(mockMedicinesRef.addValueEventListener(any())).thenReturn(null)
        `when`(mockSalesRef.addValueEventListener(any())).thenReturn(null)
        
        // Create ViewModel with mocked dependencies
        viewModel = MedexViewModel(mockAuth, mockDatabase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

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

    @Test
    fun `test removeFromCart removes medicine from cart`() {
        // Given
        val medicine = Medicine(id = "1", name = "Test Medicine", price = 10.0)
        viewModel.addToCart(medicine)

        // When
        viewModel.removeFromCart(medicine)

        // Then
        assertEquals(0, viewModel.cart.size)
        assertFalse(viewModel.cart.contains(medicine))
    }

    @Test
    fun `test clearCart removes all medicines from cart`() {
        // Given
        val medicine1 = Medicine(id = "1", name = "Test Medicine 1", price = 10.0)
        val medicine2 = Medicine(id = "2", name = "Test Medicine 2", price = 20.0)
        viewModel.addToCart(medicine1)
        viewModel.addToCart(medicine2)

        // When
        viewModel.clearCart()

        // Then
        assertEquals(0, viewModel.cart.size)
    }

    @Test
    fun `test getMedicineById returns correct medicine`() {
        // Given
        val medicine = Medicine(id = "1", name = "Test Medicine", price = 10.0)
        viewModel.medicines.add(medicine)

        // When
        val result = viewModel.getMedicineById("1")

        // Then
        assertEquals(medicine, result)
    }

    @Test
    fun `test getMedicineById returns null for non-existent medicine`() {
        // When
        val result = viewModel.getMedicineById("non-existent")

        // Then
        assertNull(result)
    }

    @Test
    fun `test getSalesForMedicine returns correct sales`() {
        // Given
        val sale1 = Sale(id = "1", medicineId = "med1", quantity = 2)
        val sale2 = Sale(id = "2", medicineId = "med1", quantity = 1)
        val sale3 = Sale(id = "3", medicineId = "med2", quantity = 3)
        viewModel.sales.addAll(listOf(sale1, sale2, sale3))

        // When
        val result = viewModel.getSalesForMedicine("med1")

        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains(sale1))
        assertTrue(result.contains(sale2))
        assertFalse(result.contains(sale3))
    }

    @Test
    fun `test isLoggedIn returns true when user is logged in`() {
        // Given
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        // When
        val result = viewModel.isLoggedIn()

        // Then
        assertTrue(result)
        verify(mockAuth, atLeastOnce()).currentUser
    }

    @Test
    fun `test isLoggedIn returns false when user is not logged in`() {
        // Given
        `when`(mockAuth.currentUser).thenReturn(null)

        // When
        val result = viewModel.isLoggedIn()

        // Then
        assertFalse(result)
        verify(mockAuth, atLeastOnce()).currentUser
    }

    @Test
    fun `test logout clears current user`() {
        // Given
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        // When
        viewModel.logout()

        // Then
        verify(mockAuth).signOut()
    }

    @Test
    fun `test recordSale with sufficient stock updates medicine and creates sale`() {
        // Given
        val medicine = Medicine(id = "1", name = "Test Medicine", price = 10.0, stock = 10)
        viewModel.medicines.add(medicine)

        // When
        viewModel.recordSale(
            medicineId = "1",
            quantity = 3,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )

        // Then
        verify(mockMedicinesRef).child("1")
        verify(mockSalesRef).child(any())
    }

    @Test
    fun `test recordSale with insufficient stock does not create sale`() {
        // Given
        val medicine = Medicine(id = "1", name = "Test Medicine", price = 10.0, stock = 2)
        viewModel.medicines.add(medicine)

        // When
        viewModel.recordSale(
            medicineId = "1",
            quantity = 5,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )

        // Then
        // Should not call salesRef.child() because sale is not created
        verify(mockSalesRef, never()).child(any())
    }

    @Test
    fun `test recordSale with non-existent medicine does not create sale`() {
        // When
        viewModel.recordSale(
            medicineId = "non-existent",
            quantity = 1,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )

        // Then
        // Should not call salesRef.child() because sale is not created
        verify(mockSalesRef, never()).child(any())
    }

    @Test
    fun `test addMedicine calls database reference`() {
        // Given
        val medicine = Medicine(id = "1", name = "Test Medicine", price = 10.0)

        // When
        viewModel.addMedicine(medicine)

        // Then
        verify(mockMedicinesRef).child("1")
    }

    @Test
    fun `test updateMedicine calls database reference`() {
        // Given
        val medicine = Medicine(id = "1", name = "Test Medicine", price = 10.0)

        // When
        viewModel.updateMedicine(medicine)

        // Then
        verify(mockMedicinesRef).child("1")
    }

    @Test
    fun `test deleteMedicine calls database reference`() {
        // When
        viewModel.deleteMedicine("1")

        // Then
        verify(mockMedicinesRef).child("1")
    }

    @Test
    fun `test cart operations maintain state correctly`() {
        // Given
        val medicine1 = Medicine(id = "1", name = "Medicine 1", price = 10.0)
        val medicine2 = Medicine(id = "2", name = "Medicine 2", price = 20.0)

        // When - Add medicines
        viewModel.addToCart(medicine1)
        viewModel.addToCart(medicine2)

        // Then
        assertEquals(2, viewModel.cart.size)

        // When - Remove one medicine
        viewModel.removeFromCart(medicine1)

        // Then
        assertEquals(1, viewModel.cart.size)
        assertFalse(viewModel.cart.contains(medicine1))
        assertTrue(viewModel.cart.contains(medicine2))

        // When - Clear cart
        viewModel.clearCart()

        // Then
        assertEquals(0, viewModel.cart.size)
    }

    @Test
    fun `test medicines list operations`() {
        // Given
        val medicine1 = Medicine(id = "1", name = "Medicine 1", price = 10.0)
        val medicine2 = Medicine(id = "2", name = "Medicine 2", price = 20.0)

        // When
        viewModel.medicines.add(medicine1)
        viewModel.medicines.add(medicine2)

        // Then
        assertEquals(2, viewModel.medicines.size)
        assertTrue(viewModel.medicines.contains(medicine1))
        assertTrue(viewModel.medicines.contains(medicine2))
    }

    @Test
    fun `test sales list operations`() {
        // Given
        val sale1 = Sale(id = "1", medicineId = "med1", quantity = 2)
        val sale2 = Sale(id = "2", medicineId = "med2", quantity = 1)

        // When
        viewModel.sales.add(sale1)
        viewModel.sales.add(sale2)

        // Then
        assertEquals(2, viewModel.sales.size)
        assertTrue(viewModel.sales.contains(sale1))
        assertTrue(viewModel.sales.contains(sale2))
    }
} 