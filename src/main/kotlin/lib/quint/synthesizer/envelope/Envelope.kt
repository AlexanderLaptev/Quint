package lib.quint.synthesizer.envelope

fun interface Envelope {
    fun getValue(time: Double, duration: Double): Double
}
