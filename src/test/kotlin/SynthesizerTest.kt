import lib.quint.AudioPlayer
import lib.quint.generator.WaveformGenerator
import lib.quint.synthesizer.Synthesizer
import lib.quint.synthesizer.SynthesizerSource
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main() {
    val format = AudioFormat(44100.0f, 16, 2, true, true)
    val line = AudioSystem.getSourceDataLine(format)

    val leftOscillator = Synthesizer.Oscillator(WaveformGenerator.Triangle, panning = -1.0, pitch = 0.5, volume = 2.0)
    val rightOscillator = Synthesizer.Oscillator(WaveformGenerator.Triangle, panning = 1.0, pitch = 2.0)
    val synth = Synthesizer(mutableListOf(leftOscillator, rightOscillator), volume = 0.3)
    val source = SynthesizerSource(synth, doubleArrayOf(440.0))

    val player = AudioPlayer()
    line.use {
        line.open(format, 2000)
        line.start()
        player.startAsync(source, line, framesPerBatch = 500) // Smaller buffer and batch sizes for lower latency

        repeat(6) {
            System.out.flush()
            rightOscillator.generator = WaveformGenerator.Sawtooth
            Thread.sleep(500)

            rightOscillator.generator = WaveformGenerator.Triangle
            System.out.flush()
            Thread.sleep(500)
        }

        player.stop()
        line.drain()
    }
}
