package lib.quint.chord

class ChordSequence(
    val chords: List<Chord>,
) {
    class Builder {
        private val chords = mutableListOf<Chord>()

        fun addChord(chord: Chord): Builder {
            chords += chord
            return this
        }

        fun addPause(duration: Double): Builder {
            chords += Chord(duration, DoubleArray(0))
            return this
        }

        fun build(): ChordSequence = ChordSequence(chords)
    }

    val duration: Double = run {
        var time = 0.0
        for (chord in chords) time += chord.duration
        time
    }

    fun getChordAtTime(time: Double): Chord {
        if (time < 0.0) return chords.first()
        var current = 0.0
        for (chord in chords) {
            val end = current + chord.duration
            if (current <= time && time < end) return chord
            current = end
        }
        return chords.last()
    }
}
