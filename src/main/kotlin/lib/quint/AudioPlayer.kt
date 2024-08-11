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

    @Volatile
    var elapsedFrames: Long = 0L
        private set

    @Volatile
    var elapsedSeconds: Double = 0.0
        private set

    fun start(
        source: AudioSource,
        line: SourceDataLine,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ) {
        check(!isRunning) { "Player already running" }
        doPlayback(source, line, timeoutSeconds, framesPerBatch)
        stop()
    }

    fun startAsync(
        source: AudioSource,
        line: SourceDataLine,
        isDaemon: Boolean = true,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ): Thread {
        val thread = thread(isDaemon = isDaemon) {
            start(source, line, timeoutSeconds, framesPerBatch)
        }
        return thread
    }

    fun stop() {
        isRunning = false
    }

    private fun doPlayback(
        source: AudioSource,
        line: SourceDataLine,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ) {
        isRunning = true
        elapsedFrames = 0L
        elapsedSeconds = 0.0

        val secondsPerFrame = 1.0 / line.format.sampleRate
        val buffer = ByteBuffer.allocate(framesPerBatch * line.format.frameSize)

        while (isRunning) {
            var generatedFrames = 0
            buffer.clear()
            repeat(framesPerBatch) {
                elapsedSeconds = elapsedFrames.toDouble() * secondsPerFrame
                if (!isRunning || elapsedSeconds >= timeoutSeconds) return
                when (source) {
                    is MonoAudioSource -> {
                        val sample = source.sample(elapsedSeconds)
                        SampleWriter.writeSample(buffer, line.format, sample)
                    }

                    is StereoAudioSource -> {
                        val sampleLeft = source.sampleLeft(elapsedSeconds)
                        val sampleRight = source.sampleRight(elapsedSeconds)
                        SampleWriter.writeSample(buffer, line.format, sampleLeft)
                        SampleWriter.writeSample(buffer, line.format, sampleRight)
                    }
                }
                generatedFrames++
                elapsedFrames++
            }

            val bytes = buffer.array()
            line.write(bytes, 0, generatedFrames * line.format.frameSize)
        }
    }

    fun getDefaultFramesPerBatch(format: AudioFormat) =
        (format.sampleRate * DEFAULT_BATCH_DURATION).toInt()
}
