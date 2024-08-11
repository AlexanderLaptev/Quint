package lib.quint

import lib.quint.source.AudioSource
import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource
import lib.quint.util.SampleWriter
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

class AudioPlayer {
    companion object {
        const val DEFAULT_BATCH_DURATION = 0.1

        const val DEFAULT_TIMEOUT = Double.POSITIVE_INFINITY
    }

    @Volatile
    var isRunning: Boolean = false
        private set

    fun start(
        source: AudioSource,
        line: SourceDataLine,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ) {
        check(!isRunning) { "Player already running" }
        processPlayback(source, line, timeoutSeconds, framesPerBatch)
    }

    fun startAsync(
        source: AudioSource,
        line: SourceDataLine,
        isDaemon: Boolean = true,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ): Thread {
        val thread = thread(isDaemon = isDaemon) {
            processPlayback(source, line, timeoutSeconds, framesPerBatch)
        }
        return thread
    }

    fun stop() {
        isRunning = false
    }

    private fun processPlayback(
        source: AudioSource,
        line: SourceDataLine,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ) {
        isRunning = true

        val secondsPerFrame = 1.0 / line.format.sampleRate
        val buffer = ByteBuffer.allocate(framesPerBatch * line.format.frameSize)
        var frames = 0L

        while (isRunning) {
            var generatedFrames = 0
            buffer.clear()
            repeat(framesPerBatch) {
                val time = frames.toDouble() * secondsPerFrame
                if (time >= timeoutSeconds) return@repeat
                when (source) {
                    is MonoAudioSource -> {
                        val sample = source.sample(time)
                        SampleWriter.writeSample(buffer, line.format, sample)
                    }

                    is StereoAudioSource -> {
                        val sampleLeft = source.sampleLeft(time)
                        val sampleRight = source.sampleRight(time)
                        SampleWriter.writeSample(buffer, line.format, sampleLeft)
                        SampleWriter.writeSample(buffer, line.format, sampleRight)
                    }
                }
                generatedFrames++
                frames++
            }

            val bytes = buffer.array()
            line.write(bytes, 0, generatedFrames * line.format.frameSize)
        }

        isRunning = false
    }

    fun getDefaultFramesPerBatch(format: AudioFormat) =
        (format.sampleRate * DEFAULT_BATCH_DURATION).toInt()
}
