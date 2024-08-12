package lib.quint

import lib.quint.source.AudioSource
import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource
import java.util.concurrent.ConcurrentHashMap
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

class AudioPlayer {
    companion object {
        const val DEFAULT_BATCH_DURATION = 0.1

        const val DEFAULT_TIMEOUT = Double.POSITIVE_INFINITY
    }

    interface EventListener {
        fun started(player: AudioPlayer) = Unit

        fun stopped(player: AudioPlayer) = Unit
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

    val eventListeners: MutableSet<EventListener> = ConcurrentHashMap.newKeySet()

    fun start(
        source: AudioSource,
        line: SourceDataLine,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ) {
        synchronized(this) {
            check(!isRunning) { "Player already running" }
            startInternal()
        }
        doPlayback(source, line, timeoutSeconds, framesPerBatch)
        for (l in eventListeners) l.stopped(this)
    }

    private fun startInternal() {
        isRunning = true
        elapsedFrames = 0L
        elapsedSeconds = 0.0
        for (l in eventListeners) l.started(this)
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

    @Synchronized
    fun stop() {
        isRunning = false
    }

    private fun doPlayback(
        source: AudioSource,
        line: SourceDataLine,
        timeoutSeconds: Double = DEFAULT_TIMEOUT,
        framesPerBatch: Int = getDefaultFramesPerBatch(line.format),
    ) {
        check(line.format.channels == when (source) {
            is MonoAudioSource -> 1
            is StereoAudioSource -> 2
        }) { "Audio source and line channel counts do not match" }
        val secondsPerFrame = 1.0 / line.format.sampleRate
        val buffer = AudioWriter.allocateBuffer(line.format, framesPerBatch)

        while (isRunning && elapsedSeconds < timeoutSeconds) {
            buffer.clear()
            AudioWriter.generateFrames(source, buffer, line.format, framesPerBatch, elapsedSeconds)
            val bytes = buffer.array()
            line.write(bytes, 0, bytes.size)
            elapsedFrames += framesPerBatch
            elapsedSeconds = elapsedFrames * secondsPerFrame
        }
    }

    private fun getDefaultFramesPerBatch(format: AudioFormat) =
        (format.sampleRate * DEFAULT_BATCH_DURATION).toInt()
}
