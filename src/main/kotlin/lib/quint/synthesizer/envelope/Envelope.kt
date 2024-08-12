package lib.quint.synthesizer.envelope

/**
 * An [Envelope] is used for modifying certain parameters over time.
 *
 * @see AdsrEnvelope
 */
fun interface Envelope {
    fun getValue(time: Double, duration: Double): Double
}
