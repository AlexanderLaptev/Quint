package lib.quint.generator

/**
 * A generator of waves.
 */
interface Generator {
    /**
     * Samples a wave of the given frequency at the given time.
     *
     * @param time the time of the sample
     * @param frequency the frequency of the wave
     * @param phase the phase shift of the generator (from 0.0 to 1.0)
     * @return the sampled value from the wave
     */
    fun sample(time: Double, frequency: Double, phase: Double = 0.0): Double
}
