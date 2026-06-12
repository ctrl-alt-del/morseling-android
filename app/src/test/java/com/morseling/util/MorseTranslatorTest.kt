package com.morseling.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MorseTranslatorTest {

    @Test
    fun textToMorse_convertsSingleWord() {
        assertEquals("... --- ...", MorseTranslator.textToMorse("SOS"))
    }

    @Test
    fun textToMorse_convertsMultipleWords() {
        assertEquals(".... . .-.. .-.. --- / .-- --- .-. .-.. -..", MorseTranslator.textToMorse("Hello World"))
    }

    @Test
    fun textToMorse_handlesNumbers() {
        assertEquals("... .---- ..---", MorseTranslator.textToMorse("S12"))
    }

    @Test
    fun textToMorse_handlesLowercase() {
        assertEquals("... --- ...", MorseTranslator.textToMorse("sos"))
    }

    @Test
    fun textToMorse_skipsPunctuation() {
        val result = MorseTranslator.textToMorse("Hello!")
        assertTrue(result.startsWith(".... . .-.. .-.. ---"))
    }

    @Test
    fun hasConvertibleChars_returnsTrueForValid() {
        assertTrue(MorseTranslator.hasConvertibleChars("ABC"))
    }

    @Test
    fun hasConvertibleChars_returnsFalseForOnlyPunctuation() {
        assertFalse(MorseTranslator.hasConvertibleChars("!@#$"))
    }

    @Test
    fun unconvertibleChars_findsPunctuation() {
        val chars = MorseTranslator.unconvertibleChars("Hello!")
        assertTrue('!' in chars)
    }

    @Test
    fun textToMorse_convertsChineseViaPinyin() {
        val result = MorseTranslator.textToMorse("你好")
        assertFalse(result.isEmpty())
        assertTrue(result.all { ch -> ch in ".- /" || ch.isWhitespace() })
    }
}
