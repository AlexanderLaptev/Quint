package lib.quint.source.adapter

import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource

class StereoToMonoAdapter(
    var stereoSource: StereoAudioSource,
    var balance: Double = 0.0,
) : MonoAudioSource {
    override fun sample(time: Double): Double {
        val rightWeight = (balance + 1.0) / 2.0
        val leftWeight = 1.0 - rightWeight
        val left = stereoSource.sampleLeft(time) * leftWeight
        val right = stereoSource.sampleRight(time) * rightWeight
        return left + right
    }
}
