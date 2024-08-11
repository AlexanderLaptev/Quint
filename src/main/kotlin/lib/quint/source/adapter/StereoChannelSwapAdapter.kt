package lib.quint.source.adapter

import lib.quint.source.StereoAudioSource

class StereoChannelSwapAdapter(
    var stereoSource: StereoAudioSource,
) : StereoAudioSource {
    override fun sampleLeft(time: Double): Double = stereoSource.sampleRight(time)

    override fun sampleRight(time: Double): Double = stereoSource.sampleLeft(time)
}
