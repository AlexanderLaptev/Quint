package lib.quint.synthesizer

import lib.quint.source.StereoAudioSource

class SynthesizerSource(
    var synthesizer: Synthesizer,
    var frequencies: DoubleArray,
) : StereoAudioSource {
    override fun sampleLeft(time: Double): Double = synthesizer.sampleLeft(time, frequencies)

    override fun sampleRight(time: Double): Double = synthesizer.sampleRight(time, frequencies)
}
