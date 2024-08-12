package lib.quint.source.adapter

import lib.quint.source.StereoAudioSource

/**
 * An adapter for swapping the channels of a [StereoAudioSource].
 *
 * @param stereoSource the stereo source whose channels to swap
 */
class StereoChannelSwapAdapter(
    var stereoSource: StereoAudioSource,
) : StereoAudioSource {
    override fun sampleLeft(time: Double): Double = stereoSource.sampleRight(time)

    override fun sampleRight(time: Double): Double = stereoSource.sampleLeft(time)
}
