package com.morseling.audio

import org.junit.Assert.assertEquals
import org.junit.Test

class MorseCodeConverterTest {

    @Test
    fun sanitizeReplacesCorrectly() {
        val result = MorseCodeConverter.sanitize("... --- ...")
        assertEquals("00012221000", result)
    }

    @Test
    fun sanitizeHandlesSlashes() {
        val result = MorseCodeConverter.sanitize("./-")
        assertEquals("032", result)
    }

    @Test
    fun sanitizePreservesLength() {
        val input = "... --- ..."
        assertEquals(input.length, MorseCodeConverter.sanitize(input).length)
    }
}
