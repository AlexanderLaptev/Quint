package lib.quint.util

import kotlin.math.log
import kotlin.math.pow

class PitchConverter(
    val tuning: Double = STANDARD_TUNING,
) {
    companion object {
        const val STANDARD_TUNING = 440.0

        const val MIDI_A4 = 69

        val DEFAULT = PitchConverter(STANDARD_TUNING)

        private const val MIDI_C0 = 12
    }

    fun midiToFrequency(code: Int): Double =
        tuning * (2.0).pow((code - MIDI_A4) / 12.0)

    fun nameToFrequency(name: String): Double = midiToFrequency(nameToMidi(name))

    fun nameToMidi(name: String): Int {
        when (name.length) {
            2 -> {
                val note = parseNote(name[0])
                val octave = name[1].digitToInt()
                return MIDI_C0 + note + 12 * octave
            }

            3 -> {
                var note = parseNote(name[0])

                when (name[1].lowercaseChar()) {
                    '#', 's', '♯' -> note++
                    'b', 'f', '♭' -> note--
                    else -> throw IllegalArgumentException("Unknown alteration symbol: ${name[1]}")
                }

                val octave = name[2].digitToInt()
                return MIDI_C0 + note + 12 * octave
            }

            else -> throw IllegalArgumentException("Pitch name must be either 2 or 3 characters long")
        }
    }

    fun midiToName(code: Int): String {
        val shifted = code - MIDI_C0
        val octave = shifted / 12
        val note = shifted % 12

        val result = StringBuilder()
        result.append(
            when (note % 12) {
                0 -> "C"
                1 -> "C#"
                2 -> "D"
                3 -> "D#"
                4 -> "E"
                5 -> "F"
                6 -> "F#"
                7 -> "G"
                8 -> "G#"
                9 -> "A"
                10 -> "A#"
                11 -> "B"
                else -> ""
            }
        )
        result.append(octave)
        return result.toString()
    }

    fun frequencyToMidi(frequency: Double): Double =
        12.0 * log(frequency / tuning, 2.0) + 69.0

    fun transposeCents(frequency: Double, cents: Int): Double =
        frequency * (2.0).pow(cents / 1200.0)

    fun transposeSemitones(frequency: Double, semitones: Int): Double =
        frequency * (2.0).pow(semitones / 12.0)

    private fun parseNote(note: Char): Int = when (note.lowercaseChar()) {
        'c' -> 0
        'd' -> 2
        'e' -> 4
        'f' -> 5
        'g' -> 7
        'a' -> 9
        'b', 'h' -> 11
        else -> error("Unknown note: $note")
    }
}
