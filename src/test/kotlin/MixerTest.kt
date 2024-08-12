import lib.quint.AudioPlayer
import lib.quint.generator.WaveformGenerator
import lib.quint.source.Mixer
import lib.quint.source.StereoAudioSource
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main() {
    val source1 = object : StereoAudioSource {
        override fun sampleLeft(time: Double): Double {
            return WaveformGenerator.Square.sample(time, 220.0) * 0.3
        }

        override fun sampleRight(time: Double): Double {
            return WaveformGenerator.Square.sample(time, 220.0) * 0.3
        }
    }

    val source2 = object : StereoAudioSource {
        override fun sampleLeft(time: Double): Double {
            return WaveformGenerator.Sine.sample(time, 880.0) * 0.3
        }

        override fun sampleRight(time: Double): Double {
            return WaveformGenerator.Sine.sample(time, 880.0) * 0.3
        }
    }

    val mixer = Mixer()
    mixer.slots += Mixer.Slot(source1, Mixer.Params(volume = 0.5, -2.0))
    mixer.slots += Mixer.Slot(source2, Mixer.Params(volume = 2.0, 2.0))

    val format = AudioFormat(44100.0f, 16, 2, true, true)
    val line = AudioSystem.getSourceDataLine(format)

    val player = AudioPlayer()
    line.use {
        line.open(format)

        line.start()
        player.startAsync(mixer, line)
        Thread.sleep(1000)
        player.stop()
        line.flush()
        line.drain()

        Thread.sleep(500)
        with(mixer.slots[0].params) {
            volume = 1.0
            balance = 0.0
        }
        with(mixer.slots[1].params) {
            volume = 1.0
            balance = 0.0
        }

        line.start()
        player.startAsync(mixer, line)
        Thread.sleep(1000)
        line.stop()
        player.stop()
    }
}
