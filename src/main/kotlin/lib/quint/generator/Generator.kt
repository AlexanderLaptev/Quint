package lib.quint.generator

interface Generator {
    fun sample(time: Double, frequency: Double, phase: Double = 0.0): Double
}
