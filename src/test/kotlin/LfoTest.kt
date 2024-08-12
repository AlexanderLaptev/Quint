import lib.quint.AudioPlayer
import lib.quint.generator.WaveformGenerator
import lib.quint.synthesizer.LowFrequencyOscillator
import lib.quint.synthesizer.Synthesizer
import lib.quint.synthesizer.SynthesizerSource
import lib.quint.synthesizer.envelope.AdsrEnvelope
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main() {
    val format = AudioFormat(44100.0f, 16, 2, true, true)
    val line = AudioSystem.getSourceDataLine(format)

    val volumeLfo = LowFrequencyOscillator(WaveformGenerator.Sine, 5.0, 0.4, delay = 1.0, attack = 2.0)
    val adsr = AdsrEnvelope(0.03, 0.0, 1.0, 0.03)

    val oscillator = Synthesizer.Oscillator(WaveformGenerator.Triangle, volume = 0.5)
    val synth = Synthesizer(mutableListOf(oscillator), volumeEnvelope = adsr, volumeLfo = volumeLfo)
    val source = SynthesizerSource(synth, doubleArrayOf(440.0))

    val player = AudioPlayer()
    line.use {
        line.open(format)
        line.start()

        player.startAsync(source, line)
        Thread.sleep(5000)

        line.stop()
        player.stop()
        line.drain()
    }
}
