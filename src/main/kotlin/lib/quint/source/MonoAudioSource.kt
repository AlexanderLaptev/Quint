package lib.quint.source

interface MonoAudioSource : AudioSource {
    fun sample(time: Double): Double
}
