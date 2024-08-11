import lib.quint.AudioPlayer
import lib.quint.source.StereoAudioSource
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin

fun main() {
    val source = object : StereoAudioSource {
        override fun sampleLeft(time: Double): Double {
            val t = (time % (1.0 / 220.0))
            return sin(2.0 * PI * 220.0 * t)
        }

        override fun sampleRight(time: Double): Double {
            val t = (time % (1.0 / 880.0))
            return sin(2.0 * PI * 880.0 * t)
        }
    }
    val player = AudioPlayer()

    val format = AudioFormat(44100.0f, 16, 2, true, true)
    val line = AudioSystem.getSourceDataLine(format)

    line.use {
        line.open(format)
        line.start()
        player.startAsync(source, line)
        Thread.sleep(1000)
        player.stop()
        line.close()
    }
}
