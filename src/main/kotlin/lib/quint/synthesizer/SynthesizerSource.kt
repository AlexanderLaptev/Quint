package lib.quint.synthesizer

import lib.quint.source.StereoAudioSource

/**
 * A source used for playing one or multiple frequencies through a
 * [Synthesizer].
 *
 * @param synthesizer the synthesizer to use
 * @param frequencies an array of frequencies to play
 * @see Synthesizer
 */
class SynthesizerSource(
    var synthesizer: Synthesizer,
    var frequencies: DoubleArray,
) : StereoAudioSource {
    override fun sampleLeft(time: Double): Double = synthesizer.sampleLeft(time, frequencies)

    override fun sampleRight(time: Double): Double = synthesizer.sampleRight(time, frequencies)
}
