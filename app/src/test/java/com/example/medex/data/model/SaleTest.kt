package com.example.medex.data.model

import org.junit.Test
import org.junit.Assert.*

class SaleTest {

    @Test
    fun `test Sale default values`() {
        // When
        val sale = Sale()

        // Then
        assertEquals("", sale.id)
        assertEquals("", sale.medicineId)
        assertEquals(0, sale.quantity)
        assertEquals(0L, sale.saleDate)
        assertEquals("", sale.userName)
        assertEquals("", sale.userAddress)
        assertEquals("", sale.userPhone)
    }

    @Test
    fun `test Sale with all parameters`() {
        // Given
        val id = "sale123"
        val medicineId = "med456"
        val quantity = 5
        val saleDate = 1640995200000L // 2022-01-01 00:00:00 UTC
        val userName = "John Doe"
        val userAddress = "123 Main St, City, State"
        val userPhone = "1234567890"

        // When
        val sale = Sale(
            id = id,
            medicineId = medicineId,
            quantity = quantity,
            saleDate = saleDate,
            userName = userName,
            userAddress = userAddress,
            userPhone = userPhone
        )

        // Then
        assertEquals(id, sale.id)
        assertEquals(medicineId, sale.medicineId)
        assertEquals(quantity, sale.quantity)
        assertEquals(saleDate, sale.saleDate)
        assertEquals(userName, sale.userName)
        assertEquals(userAddress, sale.userAddress)
        assertEquals(userPhone, sale.userPhone)
    }

    @Test
    fun `test Sale copy method`() {
        // Given
        val original = Sale(
            id = "sale123",
            medicineId = "med456",
            quantity = 5,
            saleDate = 1640995200000L,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )

        // When
        val updated = original.copy(
            quantity = 10,
            userName = "Jane Doe",
            userAddress = "456 Oak Ave"
        )

        // Then
        assertEquals("sale123", updated.id)
        assertEquals("med456", updated.medicineId)
        assertEquals(10, updated.quantity)
        assertEquals(1640995200000L, updated.saleDate)
        assertEquals("Jane Doe", updated.userName)
        assertEquals("456 Oak Ave", updated.userAddress)
        assertEquals("1234567890", updated.userPhone)
    }

    @Test
    fun `test Sale equals and hashCode`() {
        // Given
        val sale1 = Sale(
            id = "sale123",
            medicineId = "med456",
            quantity = 5,
            saleDate = 1640995200000L,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )
        val sale2 = Sale(
            id = "sale123",
            medicineId = "med456",
            quantity = 5,
            saleDate = 1640995200000L,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )
        val sale3 = Sale(
            id = "sale456",
            medicineId = "med789",
            quantity = 10,
            saleDate = 1641081600000L,
            userName = "Jane Doe",
            userAddress = "456 Oak Ave",
            userPhone = "0987654321"
        )

        // Then
        assertEquals(sale1, sale2)
        assertNotEquals(sale1, sale3)
        assertEquals(sale1.hashCode(), sale2.hashCode())
        assertNotEquals(sale1.hashCode(), sale3.hashCode())
    }

    @Test
    fun `test Sale toString`() {
        // Given
        val sale = Sale(
            id = "sale123",
            medicineId = "med456",
            quantity = 5,
            saleDate = 1640995200000L,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )

        // When
        val result = sale.toString()

        // Then
        assertTrue(result.contains("id=sale123"))
        assertTrue(result.contains("medicineId=med456"))
        assertTrue(result.contains("quantity=5"))
        assertTrue(result.contains("saleDate=1640995200000"))
        assertTrue(result.contains("userName=John Doe"))
        assertTrue(result.contains("userAddress=123 Main St"))
        assertTrue(result.contains("userPhone=1234567890"))
    }

    @Test
    fun `test Sale component functions`() {
        // Given
        val sale = Sale(
            id = "sale123",
            medicineId = "med456",
            quantity = 5,
            saleDate = 1640995200000L,
            userName = "John Doe",
            userAddress = "123 Main St",
            userPhone = "1234567890"
        )

        // When
        val (id, medicineId, quantity, saleDate, userName, userAddress, userPhone) = sale

        // Then
        assertEquals("sale123", id)
        assertEquals("med456", medicineId)
        assertEquals(5, quantity)
        assertEquals(1640995200000L, saleDate)
        assertEquals("John Doe", userName)
        assertEquals("123 Main St", userAddress)
        assertEquals("1234567890", userPhone)
    }

    @Test
    fun `test Sale with zero quantity`() {
        // When
        val sale = Sale(
            id = "sale123",
            medicineId = "med456",
            quantity = 0,
            userName = "John Doe"
        )

        // Then
        assertEquals(0, sale.quantity)
        assertEquals("John Doe", sale.userName)
    }

    @Test
    fun `test Sale with large quantity`() {
        // When
        val sale = Sale(
            id = "sale123",
            medicineId = "med456",
            quantity = 999,
            userName = "John Doe"
        )

        // Then
        assertEquals(999, sale.quantity)
    }

    @Test
    fun `test Sale with current timestamp`() {
        // Given
        val currentTime = System.currentTimeMillis()

        // When
        val sale = Sale(
            id = "sale123",
            medicineId = "med456",
            saleDate = currentTime,
            userName = "John Doe"
        )

        // Then
        assertEquals(currentTime, sale.saleDate)
    }

    @Test
    fun `test Sale with empty strings`() {
        // When
        val sale = Sale(
            id = "",
            medicineId = "",
            userName = "",
            userAddress = "",
            userPhone = ""
        )

        // Then
        assertEquals("", sale.id)
        assertEquals("", sale.medicineId)
        assertEquals("", sale.userName)
        assertEquals("", sale.userAddress)
        assertEquals("", sale.userPhone)
    }
} 