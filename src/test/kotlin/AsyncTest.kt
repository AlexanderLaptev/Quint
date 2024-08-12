import lib.quint.AudioPlayer
import lib.quint.generator.WaveformGenerator
import lib.quint.source.StereoAudioSource
import lib.quint.util.PitchConverter
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

fun main() {
    Thread.currentThread().name = "Main"

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
    val line = AudioSystem.getSourceDataLine(format)

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
            line.stop()
            player.stop()
        }
    }

    line.use {
        line.open(format)
        line.start()
        player.startAsync(source, line, timeoutSeconds = 5.0)
        Thread.sleep(5100)
    }
}
