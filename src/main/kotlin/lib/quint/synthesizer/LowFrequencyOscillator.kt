package lib.quint.synthesizer

import lib.quint.generator.Generator

open class LowFrequencyOscillator(
    var oscillator: Generator,
    var frequency: Double,
    var amplitude: Double,
    var phase: Double = 0.0,
    var attack: Double = 0.0,
    var delay: Double = 0.0,
) {
    open fun getValue(time: Double): Double {
        if (time < delay) return 0.0
        val afterDelay = time - delay
        val attackFactor = if (afterDelay < attack) afterDelay / attack else 1.0
        return oscillator.sample(afterDelay, frequency, phase) * attackFactor * amplitude
    }
}
