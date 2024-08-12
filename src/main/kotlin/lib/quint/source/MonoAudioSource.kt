package lib.quint.source

/**
 * A source of mono audio.
 *
 * @see StereoAudioSource
 */
interface MonoAudioSource : AudioSource {
    /**
     * Samples this source at the specified time.
     *
     * @param time the time of the sample
     * @return the sampled value
     */
    fun sample(time: Double): Double
}
