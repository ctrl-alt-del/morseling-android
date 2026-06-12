package com.morseling.util

import com.github.promeg.pinyinhelper.Pinyin

object MorseTranslator {

    fun textToMorse(text: String): String {
        val pinyinText = toPinyinIfCjk(text)
        return pinyinText.uppercase()
            .split(Regex("\\s+"))
            .joinToString(" / ") { word ->
                word.map { char -> CHAR_TO_MORSE[char] ?: char.toString() }
                    .joinToString(" ")
            }
    }

    fun hasConvertibleChars(text: String): Boolean {
        val pinyinText = toPinyinIfCjk(text)
        return pinyinText.uppercase().any { it in CHAR_TO_MORSE }
    }

    fun unconvertibleChars(text: String): Set<Char> {
        val pinyinText = toPinyinIfCjk(text)
        return pinyinText.uppercase()
            .filter { it !in CHAR_TO_MORSE && !it.isWhitespace() }
            .toSet()
    }

    private fun toPinyinIfCjk(text: String): String {
        val sb = StringBuilder()
        text.codePoints().forEach { cp ->
            val ch = String(Character.toChars(cp))
            if (isCjk(cp)) {
                sb.append(Pinyin.toPinyin(ch, ""))
            } else {
                sb.append(ch)
            }
        }
        val result = sb.toString()
        // If the result is the same as input, no CJK was found
        return result
    }

    private fun isCjk(codePoint: Int): Boolean =
        Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN

    private val CHAR_TO_MORSE = mapOf(
        'A' to ".-",    'B' to "-...",  'C' to "-.-.",  'D' to "-..",
        'E' to ".",     'F' to "..-.",  'G' to "--.",   'H' to "....",
        'I' to "..",    'J' to ".---",  'K' to "-.-",   'L' to ".-..",
        'M' to "--",    'N' to "-.",    'O' to "---",   'P' to ".--.",
        'Q' to "--.-",  'R' to ".-.",   'S' to "...",   'T' to "-",
        'U' to "..-",   'V' to "...-",  'W' to ".--",   'X' to "-..-",
        'Y' to "-.--",  'Z' to "--..",

        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----.",
    )
}
