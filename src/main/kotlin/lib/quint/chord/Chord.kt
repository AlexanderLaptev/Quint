package lib.quint.chord

import lib.quint.util.PitchConverter

class Chord(
    val duration: Double,
    val frequencies: DoubleArray,
) {
    class Builder(
        val duration: Double,
        val pitchConverter: PitchConverter = PitchConverter.DEFAULT,
    ) {
        private val frequencies = mutableListOf<Double>()

        fun addFrequency(frequency: Double): Builder {
            frequencies += frequency
            return this
        }

        fun addMidiNote(code: Int): Builder {
            frequencies += pitchConverter.midiToFrequency(code)
            return this
        }

        fun addNote(name: String): Builder {
            frequencies += pitchConverter.nameToFrequency(name)
            return this
        }

        fun build(): Chord = Chord(duration, frequencies.toDoubleArray())
    }

    init {
        require(duration > 0.0) { "Duration must be positive" }
        require(duration.isFinite()) { "Duration cannot be infinite" }
    }
}
