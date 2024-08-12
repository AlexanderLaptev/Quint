package lib.quint.synthesizer.envelope

interface Envelope {
    fun getValue(time: Double, duration: Double): Double
}
