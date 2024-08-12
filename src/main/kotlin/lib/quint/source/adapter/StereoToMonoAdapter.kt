package lib.quint.source.adapter

import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource

/**
 * An adapter from a [StereoAudioSource] to a [MonoAudioSource].
 *
 * @param stereoSource the stereo source to adapt
 * @param balance the balance applied to the original source before mixing
 *   it down to mono
 */
class StereoToMonoAdapter(
    var stereoSource: StereoAudioSource,
    var balance: Double = 0.0,
) : MonoAudioSource {
    override fun sample(time: Double): Double {
        val leftVolume = if (balance <= 0.0) 1.0 else clamp(1.0 - balance)
        val rightVolume = if (balance >= 0.0) 1.0 else clamp(1.0 + balance)
        val leftSample = stereoSource.sampleLeft(time) * leftVolume
        val rightSample = stereoSource.sampleRight(time) * rightVolume
        return (leftSample + rightSample) / 2.0
    }

    private fun clamp(value: Double): Double =
        if (value > 1.0) 1.0 else if (value < 0.0) 0.0 else value
}
