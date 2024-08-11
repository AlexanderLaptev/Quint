import lib.quint.AudioPlayer
import lib.quint.generator.WaveformGenerator
import lib.quint.source.StereoAudioSource
import lib.quint.source.adapter.StereoToMonoAdapter
import lib.quint.util.PitchConverter
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main() {
    val source = object : StereoAudioSource {
        val frequencyLeft = with(PitchConverter.DEFAULT) {
            transposeSemitones(nameToFrequency("G#3"), 1)
        }

        val frequencyRight = with(PitchConverter.DEFAULT) {
            transposeSemitones(nameToFrequency("A#5"), -1)
        }

        override fun sampleLeft(time: Double): Double {
            return WaveformGenerator.Square.sample(time, frequencyLeft) * 0.3
        }

        override fun sampleRight(time: Double): Double {
            return WaveformGenerator.Sawtooth.sample(time, frequencyRight) * 0.3
        }
    }

    val adapterFullLeft = StereoToMonoAdapter(source, -5.0) // No clipping or distortion should occur.
    val adapterEqual = StereoToMonoAdapter(source, 0.0)
    val adapterFullRight = StereoToMonoAdapter(source, 5.0)

    val format = AudioFormat(44100.0f, 16, 1, true, true)
    val line = AudioSystem.getSourceDataLine(format)

    val player = AudioPlayer()

    line.use {
        line.open(format)

        line.start()
        player.startAsync(adapterFullLeft, line)
        Thread.sleep(1000)
        line.stop()
        player.stop()
        line.flush()

        line.start()
        player.startAsync(adapterEqual, line)
        Thread.sleep(1000)
        line.stop()
        player.stop()
        line.flush()

        line.start()
        player.startAsync(adapterFullRight, line)
        Thread.sleep(1000)
        line.stop()
        player.stop()
        line.flush()
    }
}
