package lib.quint

import lib.quint.source.AudioSource
import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource
import lib.quint.util.SampleWriter
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

/**
 * A utility object for writing raw audio data.
 */
object AudioWriter {
    /**
     * Generates raw audio data of the specified duration in seconds and writes
     * it to a buffer.
     *
     * @param source the audio source
     * @param buffer the buffer to write to
     * @param format the audio format being used
     * @param seconds the duration of the audio in seconds
     * @param timeShift the time shift to use when sampling the source
     */
    fun generateSeconds(
        source: AudioSource,
        buffer: ByteBuffer,
        format: AudioFormat,
        seconds: Double,
        timeShift: Double = 0.0,
    ) {
        generateFrames(source, buffer, format, (seconds * format.sampleRate).toInt(), timeShift)
    }

    /**
     * Generates raw audio data of the specified duration in frames and writes
     * it to a buffer.
     *
     * @param source the audio source
     * @param buffer the buffer to write to
     * @param format the audio format being used
     * @param frames the duration of the audio in frames
     * @param timeShift the time shift to use when sampling the source
     */
    fun generateFrames(
        source: AudioSource,
        buffer: ByteBuffer,
        format: AudioFormat,
        frames: Int,
        timeShift: Double = 0.0,
    ) {
        check(buffer.remaining() >= frames * format.frameSize) {
            "Buffer cannot fit the specified number of frames"
        }

        val secondsPerFrame = 1.0 / format.sampleRate
        var generatedFrames = 0L

        while (generatedFrames < frames) {
            val time = timeShift + generatedFrames.toDouble() * secondsPerFrame
            when (source) {
                is MonoAudioSource -> {
                    val sample = source.sample(time)
                    SampleWriter.writeSample(buffer, format, sample)
                }

                is StereoAudioSource -> {
                    val sampleLeft = source.sampleLeft(time)
                    val sampleRight = source.sampleRight(time)
                    SampleWriter.writeSample(buffer, format, sampleLeft)
                    SampleWriter.writeSample(buffer, format, sampleRight)
                }
            }
            generatedFrames++
        }
    }

    /**
     * Allocates a byte buffer capable of storing the specified number of audio
     * frames.
     *
     * @param format the audio format being used
     * @param frames the requested capacity of the buffer in frames
     * @return the newly allocated byte buffer
     */
    fun allocateBuffer(format: AudioFormat, frames: Int): ByteBuffer =
        ByteBuffer.allocate(frames * format.frameSize)
}
