package lib.quint.util

import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

object SampleWriter {
    const val BYTE_MIN_SIGNED = -128.0
    const val BYTE_MAX_SIGNED = 127.0
    const val BYTE_MAX_UNSIGNED = 255.0

    const val SHORT_MIN_SIGNED = -32768.0
    const val SHORT_MAX_SIGNED = 32767.0
    const val SHORT_MAX_UNSIGNED = 65535.0

    fun toByte(value: Double, isSigned: Boolean): Byte = mapSampleToOutRange(
        clampToRange(value),
        if (isSigned) BYTE_MIN_SIGNED else 0.0,
        if (isSigned) BYTE_MAX_SIGNED else BYTE_MAX_UNSIGNED
    ).toInt().toByte()

    fun toShort(value: Double, isSigned: Boolean): Short = mapSampleToOutRange(
        clampToRange(value),
        if (isSigned) SHORT_MIN_SIGNED else 0.0,
        if (isSigned) SHORT_MAX_SIGNED else SHORT_MAX_UNSIGNED
    ).toInt().toShort()

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
