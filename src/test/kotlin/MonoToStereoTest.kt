import lib.quint.AudioPlayer
import lib.quint.generator.WaveformGenerator
import lib.quint.source.MonoAudioSource
import lib.quint.source.adapter.MonoToStereoAdapter
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main() {
    val source = object : MonoAudioSource {
        override fun sample(time: Double): Double {
            return WaveformGenerator.Triangle.sample(time, 440.0) * 0.3
        }
    }

    val adapterFullLeft = MonoToStereoAdapter(source, -5.0) // No clipping or distortion should occur.
    val adapterEqual = MonoToStereoAdapter(source, 0.0)
    val adapterFullRight = MonoToStereoAdapter(source, 5.0)

    val format = AudioFormat(44100.0f, 16, 2, true, true)
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
