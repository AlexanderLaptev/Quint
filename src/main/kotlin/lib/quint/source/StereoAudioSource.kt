package lib.quint.source

interface StereoAudioSource : AudioSource {
    fun sampleLeft(time: Double): Double

    fun sampleRight(time: Double): Double
}
