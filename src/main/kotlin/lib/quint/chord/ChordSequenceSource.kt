package lib.quint.chord

import lib.quint.source.StereoAudioSource

class ChordSequenceSource(
    chordSequence: ChordSequence
) : StereoAudioSource {
    private var chordIndex = 0

    private var chordEndTime = 0.0

    private var currentChord: Chord? = null

    var chordSequence: ChordSequence = chordSequence
        set(value) {
            field = value
            reset()
        }

    init {
        this.chordSequence = chordSequence
    }

    fun reset() {
        currentChord = chordSequence.chords.firstOrNull()
        chordIndex = 0
        chordEndTime = currentChord?.duration ?: 0.0
    }

    override fun sampleLeft(time: Double): Double {
        updateCurrentChord(time)
        val chord = currentChord ?: return 0.0
        return chord.synthesizer.sampleLeft(time, chord.frequencies)
    }

    override fun sampleRight(time: Double): Double {
        updateCurrentChord(time)
        val chord = currentChord ?: return 0.0
        return chord.synthesizer.sampleRight(time, chord.frequencies)
    }

    private fun updateCurrentChord(time: Double) {
        if (time <= chordEndTime) return
        while (time > chordEndTime) {
            chordIndex++
            currentChord = chordSequence.chords.getOrNull(chordIndex)
            chordEndTime += currentChord?.duration ?: Double.POSITIVE_INFINITY
        }
    }
}
