package lib.quint.util

import kotlin.math.log
import kotlin.math.pow

/**
 * A helper object for converting between different representations of
 * pitches.
 *
 * @param tuning the frequency of the A4 note
 */
class PitchConverter(
    val tuning: Double = STANDARD_TUNING,
) {
    companion object {
        /**
         * The standard tuning used by the default instance.
         *
         * @see DEFAULT
         */
        const val STANDARD_TUNING = 440.0

        /**
         * The MIDI code of the A4 note.
         */
        const val MIDI_A4 = 69

        /**
         * The default instance of [PitchConverter] using the default tuning.
         *
         * @see STANDARD_TUNING
         */
        val DEFAULT = PitchConverter(STANDARD_TUNING)

        private const val MIDI_C0 = 12
    }

    /**
     * Converts the given MIDI code to the corresponding frequency.
     *
     * @param code the MIDI code to convert
     * @return the corresponding frequency
     */
    fun midiToFrequency(code: Int): Double =
        tuning * (2.0).pow((code - MIDI_A4) / 12.0)

    /**
     * Converts the given note name to the corresponding frequency.
     *
     * The conversion is case-insensitive. The first character is always the
     * pitch of the note (valid pitches are C, D, E, F, G, A, B/H) followed
     * by an optional alteration sign (either of #, s, ♯, b, f, ♭). The last
     * character is always a digit representing the octave of the note. Valid
     * examples: C2, A#4, h5.
     *
     * @param name the name of the pitch adhering to the above format
     * @return the corresponding frequency
     */
    fun nameToFrequency(name: String): Double = midiToFrequency(nameToMidi(name))

    /**
     * Converts the given note name to a MIDI code.
     *
     * @param name the name of the note in the same format recognized by
     *   [nameToFrequency]
     * @return the MIDI code corresponding to the note
     * @see nameToFrequency
     */
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

    /**
     * Converts the given MIDI code to a note name.
     *
     * @param code the MIDI code to convert
     * @return the name of the corresponding note
     */
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

    /**
     * Approximately converts the given frequency to a MIDI code.
     *
     * @param frequency the frequency to convert
     * @return the corresponding MIDI code as a floating-point value
     */
    fun frequencyToMidi(frequency: Double): Double =
        12.0 * log(frequency / tuning, 2.0) + 69.0

    /**
     * Transposes the given frequency by the specified number of cents.
     *
     * @param frequency the frequency to transpose
     * @param cents the positive or negative number of cents to transpose by
     * @return the transposed frequency
     */
    fun transposeCents(frequency: Double, cents: Int): Double =
        frequency * (2.0).pow(cents / 1200.0)

    /**
     * Transposes the given frequency by the specified number of semitones.
     *
     * @param frequency the frequency to transpose
     * @param semitones the positive or negative number of semitones to
     *   transpose by
     * @return the transposed frequency
     */
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
