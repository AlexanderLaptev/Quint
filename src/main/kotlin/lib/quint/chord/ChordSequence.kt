package lib.quint.chord

import lib.quint.synthesizer.Synthesizer

/**
 * A sequence of chords played in succession.
 *
 * @param chords a list of chords in this sequence
 * @see Chord
 * @see ChordSequenceSource
 */
class ChordSequence(
    val chords: List<Chord>,
) {
    companion object {
        private val silentSynthesizer = Synthesizer()
    }

    /**
     * A builder for [ChordSequence]s.
     */
    class Builder {
        private val chords = mutableListOf<Chord>()

        /**
         * Adds the given chord to the sequence.
         *
         * @param chord the chord to add
         * @return this builder for chaining
         */
        fun addChord(chord: Chord): Builder {
            chords += chord
            return this
        }

        /**
         * Adds a pause to the sequence. A pause is a chord with no frequencies.
         *
         * @param duration the duration of the pause in seconds
         * @return this builder for chaining
         */
        fun addPause(duration: Double): Builder {
            chords += Chord(duration, silentSynthesizer, DoubleArray(0))
            return this
        }

        /**
         * Returns the [ChordSequence] described by this builder.
         *
         * @return the newly created chord sequence
         */
        fun build(): ChordSequence = ChordSequence(chords)
    }

    /**
     * The duration of the sequence in seconds
     */
    val duration: Double = run {
        var time = 0.0
        for (chord in chords) time += chord.duration
        time
    }
}
