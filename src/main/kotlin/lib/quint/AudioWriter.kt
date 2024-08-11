package lib.quint

import lib.quint.source.AudioSource
import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource
import lib.quint.util.SampleWriter
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

object AudioWriter {
    fun generateSeconds(
        source: AudioSource,
        buffer: ByteBuffer,
        format: AudioFormat,
        seconds: Double,
        timeShift: Double = 0.0,
    ) {
        generateFrames(source, buffer, format, (seconds * format.sampleRate).toInt(), timeShift)
    }

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

    fun allocateBuffer(format: AudioFormat, frames: Int): ByteBuffer =
        ByteBuffer.allocate(frames * format.frameSize)
}
