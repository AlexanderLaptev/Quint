package lib.quint.synthesizer

import lib.quint.generator.Generator

class Synthesizer(
    val oscillators: MutableCollection<Oscillator> = mutableListOf(),
    var volume: Double = 1.0,
    var pitch: Double = 1.0,
    var panning: Double = 0.0,
) {
    class Oscillator(
        var generator: Generator,
        var volume: Double = 1.0,
        var pitch: Double = 1.0,
        var panning: Double = 0.0,
        var phase: Double = 0.0,
    )

    fun sampleLeft(
        time: Double,
        frequencies: DoubleArray,
    ): Double = sample(time, frequencies, true)

    fun sampleRight(
        time: Double,
        frequencies: DoubleArray,
    ): Double = sample(time, frequencies, false)

    private fun sample(
        time: Double,
        frequencies: DoubleArray,
        isLeft: Boolean,
    ): Double {
        var output = 0.0

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
            sample *= getChannelVolume(panning, isLeft) // Apply global panning of the synthesizer
            sample /= frequencies.size // Normalize the output range

            output += sample * oscillator.volume // Scale by the oscillator's volume
        }

        return output / oscillators.size * volume // Normalize the range and scale by the global volume
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
