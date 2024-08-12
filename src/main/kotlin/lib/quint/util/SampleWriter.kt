package lib.quint.util

import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

/**
 * A utility object for writing frames of audio data to byte buffers.
 */
object SampleWriter {
    private const val BYTE_MIN_SIGNED = -128.0
    private const val BYTE_MAX_SIGNED = 127.0
    private const val BYTE_MAX_UNSIGNED = 255.0

    private const val SHORT_MIN_SIGNED = -32768.0
    private const val SHORT_MAX_SIGNED = 32767.0
    private const val SHORT_MAX_UNSIGNED = 65535.0

    /**
     * Converts the given value to a byte.
     *
     * @param value the value to convert
     * @param isSigned whether signed bytes are being used
     */
    fun toByte(value: Double, isSigned: Boolean): Byte = mapSampleToOutRange(
        clampToRange(value),
        if (isSigned) BYTE_MIN_SIGNED else 0.0,
        if (isSigned) BYTE_MAX_SIGNED else BYTE_MAX_UNSIGNED
    ).toInt().toByte()

    /**
     * Converts the given value to a short.
     *
     * @param value the value to convert
     * @param isSigned whether signed shorts are being used
     */
    fun toShort(value: Double, isSigned: Boolean): Short = mapSampleToOutRange(
        clampToRange(value),
        if (isSigned) SHORT_MIN_SIGNED else 0.0,
        if (isSigned) SHORT_MAX_SIGNED else SHORT_MAX_UNSIGNED
    ).toInt().toShort()

    /**
     * Writes the given sample value to a byte buffer.
     *
     * @param byteBuffer the byte buffer to write samples to
     * @param audioFormat the audio format being used
     * @param sampleValue the sample value to write
     */
    fun writeSample(byteBuffer: ByteBuffer, audioFormat: AudioFormat, sampleValue: Double) {
        val isSigned = when (audioFormat.encoding) {
            AudioFormat.Encoding.PCM_SIGNED -> true
            AudioFormat.Encoding.PCM_UNSIGNED -> false
            else -> error("Unsupported encoding")
        }

        when (audioFormat.sampleSizeInBits) {
            Byte.SIZE_BITS -> byteBuffer.put(toByte(sampleValue, isSigned))
            Short.SIZE_BITS -> byteBuffer.putShort(toShort(sampleValue, isSigned))
            else -> error("Unsupported sample bit depth")
        }
    }

    private fun mapSampleToOutRange(value: Double, outMin: Double, outMax: Double) =
        (value + 1.0) / 2.0 * (outMax - outMin) + outMin

    private fun clampToRange(value: Double) =
        if (value > 1.0) 1.0 else if (value < -1.0) -1.0 else value
}
