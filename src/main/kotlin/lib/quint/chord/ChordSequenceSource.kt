package lib.quint.chord

import lib.quint.source.StereoAudioSource

class ChordSequenceSource(
    chordSequence: ChordSequence
) : StereoAudioSource {
    private var chordIndex = 0

    private var chordStartTime = 0.0

    private var chordEndTime = 0.0

    var currentChord: Chord? = null
        private set

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
        chordStartTime = 0.0
        chordEndTime = currentChord?.duration ?: 0.0
    }

    override fun sampleLeft(time: Double): Double {
        updateCurrentChord(time)
        val chord = currentChord ?: return 0.0
        return chord.synthesizer.sampleLeft(time - chordStartTime, chord.frequencies, chord.duration)
    }

    override fun sampleRight(time: Double): Double {
        updateCurrentChord(time)
        val chord = currentChord ?: return 0.0
        return chord.synthesizer.sampleRight(time - chordStartTime, chord.frequencies, chord.duration)
    }

    private fun updateCurrentChord(time: Double) {
        if (time <= chordEndTime) return
        while (time > chordEndTime) {
            chordIndex++
            currentChord = chordSequence.chords.getOrNull(chordIndex)
            chordStartTime = chordEndTime
            chordEndTime += currentChord?.duration ?: Double.POSITIVE_INFINITY
        }
    }
}
