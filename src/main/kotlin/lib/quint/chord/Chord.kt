package lib.quint.chord

import lib.quint.synthesizer.Synthesizer
import lib.quint.util.PitchConverter

/**
 * A `Chord` is a collection of frequencies all playing at the same time on
 * a given [Synthesizer].
 *
 * @param duration the duration of the chord in seconds
 * @param synthesizer the synthesizer used for playing this chord
 * @param frequencies an array of frequencies in this chord
 * @throws IllegalArgumentException if the duration is negative, zero, or
 *   infinite
 * @see Synthesizer
 * @see ChordSequence
 * @see ChordSequenceSource
 */
class Chord(
    val duration: Double,
    val synthesizer: Synthesizer,
    val frequencies: DoubleArray,
) {
    /**
     * A builder for [Chord]s.
     *
     * @param duration the duration of the chord
     * @param synthesizer the synthesizer used for playing this chord
     * @param pitchConverter the pitch converter to use in this builder
     */
    class Builder(
        val duration: Double,
        val synthesizer: Synthesizer,
        val pitchConverter: PitchConverter = PitchConverter.DEFAULT,
    ) {
        private val frequencies = mutableListOf<Double>()

        /**
         * Adds the given frequency to the chord.
         *
         * @param frequency the frequency to add
         * @return this builder for chaining
         */
        fun addFrequency(frequency: Double): Builder {
            frequencies += frequency
            return this
        }

        /**
         * Adds the given MIDI note to the chord. The conversion is done by the
         * [pitchConverter] of this builder.
         *
         * @param code the MIDI code of the note to add
         * @return this builder for chaining
         */
        fun addMidiNote(code: Int): Builder {
            frequencies += pitchConverter.midiToFrequency(code)
            return this
        }

        /**
         * Adds the given note to the chord. The conversion is done by the
         * [pitchConverter] of this builder.
         *
         * @param name the name of the note to add
         * @return this builder for chaining
         */
        fun addNote(name: String): Builder {
            frequencies += pitchConverter.nameToFrequency(name)
            return this
        }

        /**
         * Returns the [Chord] described by this builder.
         *
         * @return the newly created chord
         */
        fun build(): Chord = Chord(duration, synthesizer, frequencies.toDoubleArray())
    }

    init {
        require(duration > 0.0) { "Duration must be positive" }
        require(duration.isFinite()) { "Duration cannot be infinite" }
    }
}
