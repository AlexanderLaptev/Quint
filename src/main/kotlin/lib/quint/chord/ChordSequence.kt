package lib.quint.chord

import lib.quint.synthesizer.Synthesizer

class ChordSequence(
    val chords: List<Chord>,
) {
    companion object {
        private val silentSynthesizer = Synthesizer()
    }

    class Builder {
        private val chords = mutableListOf<Chord>()

        fun addChord(chord: Chord): Builder {
            chords += chord
            return this
        }

        fun addPause(duration: Double): Builder {
            chords += Chord(duration, silentSynthesizer, DoubleArray(0))
            return this
        }

        fun build(): ChordSequence = ChordSequence(chords)
    }

    val duration: Double = run {
        var time = 0.0
        for (chord in chords) time += chord.duration
        time
    }
}
