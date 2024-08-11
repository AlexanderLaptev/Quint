import lib.quint.AudioWriter
import lib.quint.generator.WaveformGenerator
import lib.quint.source.StereoAudioSource
import lib.quint.util.PitchConverter
import java.util.concurrent.CountDownLatch
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent

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

    val format = AudioFormat(44100.0f, 16, 2, true, true)
    val clip = AudioSystem.getClip()
    val latch = CountDownLatch(1)

    clip.addLineListener {
        if (it.type == LineEvent.Type.STOP) {
            latch.countDown()
        }
    }

    val buffer = AudioWriter.allocateBuffer(format, format.sampleRate.toInt())
    AudioWriter.generateFrames(source, buffer, format, format.sampleRate.toInt())
    clip.use {
        clip.open(format, buffer.array(), 0, buffer.capacity())
        clip.start()
        latch.await()
    }
}
