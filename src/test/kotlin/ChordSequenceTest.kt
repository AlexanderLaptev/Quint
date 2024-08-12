import lib.quint.AudioPlayer
import lib.quint.chord.Chord
import lib.quint.chord.ChordSequence
import lib.quint.chord.ChordSequenceSource
import lib.quint.generator.WaveformGenerator
import lib.quint.synthesizer.Synthesizer
import lib.quint.synthesizer.envelope.AdsrEnvelope
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main() {
    val oscillator1 = Synthesizer.Oscillator(WaveformGenerator.Triangle)
    val oscillator2 = Synthesizer.Oscillator(WaveformGenerator.Sine)
    val oscillator3 = Synthesizer.Oscillator(WaveformGenerator.Square)

    val adsr = AdsrEnvelope(0.03, 0.0, 1.0, 0.03)
    val synthesizer1 = Synthesizer(mutableListOf(oscillator1))
    val synthesizer2 = Synthesizer(mutableListOf(oscillator2))
    val synthesizer3 = Synthesizer(mutableListOf(oscillator3))
    synthesizer1.volumeEnvelope = adsr
    synthesizer2.volumeEnvelope = adsr
    synthesizer3.volumeEnvelope = adsr

    val cMajor = Chord.Builder(0.5, synthesizer1)
        .addNote("C4")
        .addNote("E4")
        .addNote("G4")
        .build()
    val fMajor = Chord.Builder(0.5, synthesizer2)
        .addNote("F4")
        .addNote("A4")
        .addNote("C4")
        .build()
    val gMajor = Chord.Builder(0.5, synthesizer3)
        .addNote("G4")
        .addNote("B4")
        .addNote("D4")
        .build()

    val chordSequence = ChordSequence.Builder()
        .addChord(cMajor)
        .addPause(0.5)
        .addChord(fMajor)
        .addPause(0.5)
        .addChord(gMajor)
        .addPause(0.5)
        .addChord(cMajor)
        .addPause(0.5)
        .build()

    val format = AudioFormat(44100.0f, 16, 2, true, true)
    val line = AudioSystem.getSourceDataLine(format)

    val source = ChordSequenceSource(chordSequence)
    val player = AudioPlayer()

    line.use {
        line.open(format)
        line.start()
        player.start(source, line, chordSequence.duration)
        line.stop()
        line.drain()
    }
}
