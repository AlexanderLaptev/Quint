package lib.quint

import lib.quint.source.AudioSource
import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource
import java.util.concurrent.ConcurrentHashMap
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

/**
 * `AudioPlayer` that plays a given [AudioSource] through a
 * [SourceDataLine].
 *
 * The player works by generating a small amount of raw audio data (called
 * a *batch*) and writing it directly to the output `SourceDataLine`.
 *
 * At any given time, only one thread may be playing through this audio
 * player. If another thread tries to start playback while the player is
 * already active on another thread, an [IllegalStateException] will be
 * thrown.
 *
 * When the player starts or finishes playing, it invokes the corresponding
 * methods on all listeners. Note that the listeners are invoked on the
 * same thread as the thread that the player is active on. [isRunning]
 * will usually report the actual state of the player when the listeners
 * are first being called (unless one of the earlier listeners or another
 * thread decides to start or stop this player). If any new listeners are
 * added by the existing ones, they will *not* be called by the player.
 *
 * @see AudioSource
 */
class AudioPlayer {
    companion object {
        /**
         * The default duration of a batch in seconds.
         */
        const val DEFAULT_BATCH_DURATION = 0.1

        /**
         * The default timeout.
         */
        const val DEFAULT_TIMEOUT = Double.POSITIVE_INFINITY
    }

    /**
     * A listener for the [AudioPlayer] events.
     */
    interface EventListener {
        /**
         * Called when the corresponding [player] starts playing.
         */
        fun started(player: AudioPlayer) = Unit

        /**
         * Called when the corresponding [player] stops playing.
         */
        fun stopped(player: AudioPlayer) = Unit
    }

    /**
     * Whether the player is currently running.
     *
     * @see start
     * @see stop
     */
    @Volatile
    var isRunning: Boolean = false
        private set

    /**
     * The number of audio frames generated thus far. This value is reset upon
     * starting the player.
     */
    @Volatile
    var elapsedFrames: Long = 0L
        private set

    /**
     * The number of seconds of audio generated thus far. This value is reset
     * upon starting the player.
     */
    @Volatile
    var elapsedSeconds: Double = 0.0
        private set

    /**
     * A concurrent set of event listeners of this player.
     *
     * @see EventListener
     */
    val eventListeners: MutableSet<EventListener> = ConcurrentHashMap.newKeySet()

    /**
     * Starts this player on the current thread, blocking either until stopped
     * by another thread or after the specified timeout in seconds.
     *
     * @param source the audio source to play
     * @param line the output line
     * @param timeoutSeconds the timeout in seconds. If no timeout is desired,
     *    positive infinity should be used.
     * @param framesPerBatch the number of frames in a batch
     * @throws IllegalStateException if the player is already running or
     * @throws IllegalStateException if the source and line channel counts do
     *    not match
     * @see startAsync
     * @see stop
     */
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

    /**
     * Starts this player in another thread.
     *
     * @param source the audio source to play
     * @param line the output line
     * @param isDaemon whether the new thread should be a daemon
     * @param timeoutSeconds the timeout in seconds
     * @param framesPerBatch the number of frames in a batch
     * @throws IllegalStateException if the player is already running
     * @throws IllegalStateException if the source and line channel counts do
     *    not match
     * @see start
     * @see stop
     */
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

    /**
     * Stops the player on the thread that is was started on.
     *
     * @see start
     * @see startAsync
     */
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
        check(
            line.format.channels == when (source) {
                is MonoAudioSource -> 1
                is StereoAudioSource -> 2
            }
        ) { "Audio source and line channel counts do not match" }
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
