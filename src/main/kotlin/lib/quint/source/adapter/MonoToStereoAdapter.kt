package lib.quint.source.adapter

import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource

class MonoToStereoAdapter(
    var monoSource: MonoAudioSource,
) : StereoAudioSource {
    override fun sampleLeft(time: Double): Double = monoSource.sample(time)

    override fun sampleRight(time: Double): Double = monoSource.sample(time)
}
