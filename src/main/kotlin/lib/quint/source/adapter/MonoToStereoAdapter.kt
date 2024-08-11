package lib.quint.source.adapter

import lib.quint.source.MonoAudioSource
import lib.quint.source.StereoAudioSource

class MonoToStereoAdapter(
    var monoSource: MonoAudioSource,
    var panning: Double = 0.0,
) : StereoAudioSource {
    override fun sampleLeft(time: Double): Double {
        val volume = if (panning <= 0.0) 1.0 else clamp(1.0 - panning)
        return monoSource.sample(time) * volume
    }

    override fun sampleRight(time: Double): Double {
        val volume = if (panning >= 0.0) 1.0 else clamp(1.0 + panning)
        return monoSource.sample(time) * volume
    }

    private fun clamp(value: Double): Double =
        if (value > 1.0) 1.0 else if (value < 0.0) 0.0 else value
}
