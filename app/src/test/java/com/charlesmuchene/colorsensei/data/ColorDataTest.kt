package com.charlesmuchene.colorsensei.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ColorDataTest {

    @Test
    fun `Get indices returns a valid randomized list of indices`() {

        val count = 10

        val indices = ColorData.getIndices(count, 100)

        assertEquals(count, indices.size)

    }

    @Test
    fun `Load colors from json reads correctly from a comma separated json array`() {
        val json = "[{\"name\":\"Absolute Zero\",\"hex\":\"#0048BA\"},{\"name\":\"Acid\",\"hex\":\"#B0BF1A\"}]"
        val stream = json.byteInputStream()

        val colors = ColorData.loadColors(stream)

        assertTrue(colors.isNotEmpty())
        assertEquals(2, colors.size)

        val firstColor = colors.first()

        assertEquals("Absolute Zero", firstColor.name)
        assertEquals("#0048BA", firstColor.hex)

    }

}