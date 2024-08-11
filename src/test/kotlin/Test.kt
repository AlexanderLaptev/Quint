import lib.quint.AudioPlayer
import lib.quint.source.StereoAudioSource
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin

fun main() {
    Thread.currentThread().name = "Main"

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

    // Listeners are invoked on the same thread as the player.
    val player = AudioPlayer()
    player.eventListeners += object : AudioPlayer.EventListener {
        override fun started(player: AudioPlayer) {
            println("[${Thread.currentThread().name}] started player")
        }

        override fun stopped(player: AudioPlayer) {
            println(
                "[${Thread.currentThread().name}] stopped player " +
                        "(${player.elapsedSeconds} seconds, ${player.elapsedFrames} frames)"
            )
        }
    }

    val format = AudioFormat(44100.0f, 16, 2, true, true)
    val line = AudioSystem.getSourceDataLine(format)

    line.use {
        line.open(format)
        line.start()
        player.startAsync(source, line, timeoutSeconds = 1.0)
        Thread.sleep(2000)
        line.close()
        player.stop()
    }
}
