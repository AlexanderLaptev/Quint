package lib.quint.source

/**
 * A source of stereo audio.
 *
 * Note that almost all audio sources in this library are stereo.
 * If you need to work with mono audio, use the adapters from the
 * [lib.quint.source.adapter] package to convert mono audio to stereo and
 * stereo audio back to mono (if needed).
 *
 * @see MonoAudioSource
 * @see lib.quint.source.adapter.MonoToStereoAdapter
 * @see lib.quint.source.adapter.StereoToMonoAdapter
 */
interface StereoAudioSource : AudioSource {
    /**
     * Samples the left channel of this source at the specified time.
     *
     * @param time the time of the sample
     * @return the sampled value
     */
    fun sampleLeft(time: Double): Double

    /**
     * Samples the right channel of this source at the specified time.
     *
     * @param time the time of the sample
     * @return the sampled value
     */
    fun sampleRight(time: Double): Double
}
