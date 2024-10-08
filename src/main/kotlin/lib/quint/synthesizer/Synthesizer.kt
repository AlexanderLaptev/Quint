package lib.quint.synthesizer

import lib.quint.generator.Generator
import lib.quint.synthesizer.Synthesizer.Oscillator
import lib.quint.synthesizer.envelope.Envelope

/**
 * A [Synthesizer] consists of several [Oscillator]s playing one or more
 * frequencies together.
 *
 * @param oscillators a mutable collection of oscillators used in this
 *   synthesizer
 * @param volume the volume of this synthesizer
 * @param pitch the pitch of this synthesizer
 * @param panning the panning of this synthesizer
 * @see Oscillator
 * @see SynthesizerSource
 */
class Synthesizer(
    val oscillators: MutableCollection<Oscillator> = mutableListOf(),
    var volume: Double = 1.0,
    var pitch: Double = 1.0,
    var panning: Double = 0.0,
) {
    /**
     * An oscillator used in a [Synthesizer].
     *
     * @param generator the generator used in this oscillator
     * @param volume the volume of this individual oscillator
     * @param pitch the pitch of this individual oscillator
     * @param panning the panning of this individual oscillator
     * @param phase the phase of this oscillator
     */
    class Oscillator(
        var generator: Generator,
        var volume: Double = 1.0,
        var pitch: Double = 1.0,
        var panning: Double = 0.0,
        var phase: Double = 0.0,
    )

    /**
     * The envelope used for modifying the resulting volume of this
     * synthesizer. If no envelope should be used, this should be set to
     * `null`.
     *
     * @see Envelope
     */
    var volumeEnvelope: Envelope? = null

    /**
     * The LFO used for modifying the resulting volume of this
     * synthesizer. If no LFO should be used, this should be set to
     * `null`.
     *
     * @see LowFrequencyOscillator
     */
    var volumeLfo: LowFrequencyOscillator? = null

    /**
     * The LFO used for modifying the resulting panning of this
     * synthesizer. If no LFO should be used, this should be set to
     * `null`.
     *
     * @see LowFrequencyOscillator
     */
    var panningLfo: LowFrequencyOscillator? = null

    fun sampleLeft(
        time: Double,
        frequencies: DoubleArray,
        duration: Double = Double.POSITIVE_INFINITY,
    ): Double = sample(time, frequencies, true, duration)

    fun sampleRight(
        time: Double,
        frequencies: DoubleArray,
        duration: Double = Double.POSITIVE_INFINITY,
    ): Double = sample(time, frequencies, false, duration)

    private fun sample(
        time: Double,
        frequencies: DoubleArray,
        isLeft: Boolean,
        duration: Double = Double.POSITIVE_INFINITY,
    ): Double {
        var output = 0.0
        if (oscillators.isEmpty() || frequencies.isEmpty()) return output

        val actualVolume = combineMultipliers(volume, time, duration, volumeEnvelope, volumeLfo)
        val actualPanning = combineMultipliers(panning, time, duration, null, panningLfo)

        for (oscillator in oscillators) {
            var sample = 0.0
            for (frequency in frequencies) {
                sample += oscillator.generator.sample(
                    time,
                    frequency * oscillator.pitch * pitch,
                    oscillator.phase
                )
            }

            sample *= getChannelVolume(oscillator.panning, isLeft) // Apply panning from the oscillator
            sample *= getChannelVolume(actualPanning, isLeft) // Apply global panning of the synthesizer
            sample /= frequencies.size // Normalize the output range

            output += sample * oscillator.volume // Scale by the oscillator's volume
        }

        return output / oscillators.size * actualVolume // Normalize the range and scale by the global volume
    }

    private fun combineMultipliers(
        value: Double,
        time: Double,
        duration: Double,
        envelope: Envelope?,
        lfo: LowFrequencyOscillator?,
    ): Double {
        var result = value
        result *= envelope?.getValue(time, duration) ?: 1.0
        result += lfo?.getValue(time) ?: 0.0
        return result
    }

    private fun getChannelVolume(panning: Double, isLeft: Boolean): Double =
        if (isLeft) {
            if (panning <= 0.0) 1.0 else clamp(1.0 - panning)
        } else {
            if (panning >= 0.0) 1.0 else clamp(1.0 + panning)
        }

    private fun clamp(value: Double): Double =
        if (value > 1.0) 1.0 else if (value < 0.0) 0.0 else value
}
