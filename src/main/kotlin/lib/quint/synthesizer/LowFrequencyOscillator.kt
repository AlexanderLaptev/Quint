package lib.quint.synthesizer

import lib.quint.generator.Generator

/**
 * A low frequency oscillator (LFO) modifies certain parameters over time
 * in an oscillating manner.
 *
 * @param oscillator the generator used in this LFO
 * @param frequency the frequency of the LFO
 * @param amplitude the amplitue of the LFO
 * @param phase the phase shift of this LFO
 * @param attack the attack time of this LFO
 * @param delay the delay of this LFO
 */
open class LowFrequencyOscillator(
    var oscillator: Generator,
    var frequency: Double,
    var amplitude: Double,
    var phase: Double = 0.0,
    var attack: Double = 0.0,
    var delay: Double = 0.0,
) {
    /**
     * Gets the value of this LFO at the given time.
     *
     * @param time the given time
     * @return the value of this LFO at the specified time
     */
    open fun getValue(time: Double): Double {
        if (time < delay) return 0.0
        val afterDelay = time - delay
        val attackFactor = if (afterDelay < attack) afterDelay / attack else 1.0
        return oscillator.sample(afterDelay, frequency, phase) * attackFactor * amplitude
    }
}
