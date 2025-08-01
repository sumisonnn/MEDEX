package com.example.medex.data.model

import org.junit.Test
import org.junit.Assert.*

class MedicineTest {

    @Test
    fun `test Medicine default values`() {
        // When
        val medicine = Medicine()

        // Then
        assertEquals("", medicine.id)
        assertEquals("", medicine.name)
        assertEquals("", medicine.description)
        assertEquals(0.0, medicine.price, 0.01)
        assertEquals(0, medicine.stock)
        assertNull(medicine.imageUrl)
        assertEquals("", medicine.category)
    }

    @Test
    fun `test Medicine with all parameters`() {
        // Given
        val id = "med123"
        val name = "Paracetamol"
        val description = "Pain reliever"
        val price = 5.99
        val stock = 100
        val imageUrl = "https://example.com/paracetamol.jpg"
        val category = "Pain Relief"

        // When
        val medicine = Medicine(
            id = id,
            name = name,
            description = description,
            price = price,
            stock = stock,
            imageUrl = imageUrl,
            category = category
        )

        // Then
        assertEquals(id, medicine.id)
        assertEquals(name, medicine.name)
        assertEquals(description, medicine.description)
        assertEquals(price, medicine.price, 0.01)
        assertEquals(stock, medicine.stock)
        assertEquals(imageUrl, medicine.imageUrl)
        assertEquals(category, medicine.category)
    }

    @Test
    fun `test Medicine copy method`() {
        // Given
        val original = Medicine(
            id = "med123",
            name = "Paracetamol",
            description = "Pain reliever",
            price = 5.99,
            stock = 100,
            imageUrl = "https://example.com/paracetamol.jpg",
            category = "Pain Relief"
        )

        // When
        val updated = original.copy(
            name = "Ibuprofen",
            price = 7.99,
            stock = 50
        )

        // Then
        assertEquals("med123", updated.id)
        assertEquals("Ibuprofen", updated.name)
        assertEquals("Pain reliever", updated.description)
        assertEquals(7.99, updated.price, 0.01)
        assertEquals(50, updated.stock)
        assertEquals("https://example.com/paracetamol.jpg", updated.imageUrl)
        assertEquals("Pain Relief", updated.category)
    }

    @Test
    fun `test Medicine equals and hashCode`() {
        // Given
        val medicine1 = Medicine(
            id = "med123",
            name = "Paracetamol",
            price = 5.99,
            stock = 100
        )
        val medicine2 = Medicine(
            id = "med123",
            name = "Paracetamol",
            price = 5.99,
            stock = 100
        )
        val medicine3 = Medicine(
            id = "med456",
            name = "Ibuprofen",
            price = 7.99,
            stock = 50
        )

        // Then
        assertEquals(medicine1, medicine2)
        assertNotEquals(medicine1, medicine3)
        assertEquals(medicine1.hashCode(), medicine2.hashCode())
        assertNotEquals(medicine1.hashCode(), medicine3.hashCode())
    }

    @Test
    fun `test Medicine toString`() {
        // Given
        val medicine = Medicine(
            id = "med123",
            name = "Paracetamol",
            description = "Pain reliever",
            price = 5.99,
            stock = 100,
            imageUrl = "https://example.com/paracetamol.jpg",
            category = "Pain Relief"
        )

        // When
        val result = medicine.toString()

        // Then
        assertTrue(result.contains("id=med123"))
        assertTrue(result.contains("name=Paracetamol"))
        assertTrue(result.contains("description=Pain reliever"))
        assertTrue(result.contains("price=5.99"))
        assertTrue(result.contains("stock=100"))
        assertTrue(result.contains("imageUrl=https://example.com/paracetamol.jpg"))
        assertTrue(result.contains("category=Pain Relief"))
    }

    @Test
    fun `test Medicine with null imageUrl`() {
        // When
        val medicine = Medicine(
            id = "med123",
            name = "Paracetamol",
            imageUrl = null
        )

        // Then
        assertEquals("med123", medicine.id)
        assertEquals("Paracetamol", medicine.name)
        assertNull(medicine.imageUrl)
    }

    @Test
    fun `test Medicine component functions`() {
        // Given
        val medicine = Medicine(
            id = "med123",
            name = "Paracetamol",
            description = "Pain reliever",
            price = 5.99,
            stock = 100,
            imageUrl = "https://example.com/paracetamol.jpg",
            category = "Pain Relief"
        )

        // When
        val (id, name, description, price, stock, imageUrl, category) = medicine

        // Then
        assertEquals("med123", id)
        assertEquals("Paracetamol", name)
        assertEquals("Pain reliever", description)
        assertEquals(5.99, price, 0.01)
        assertEquals(100, stock)
        assertEquals("https://example.com/paracetamol.jpg", imageUrl)
        assertEquals("Pain Relief", category)
    }
} 