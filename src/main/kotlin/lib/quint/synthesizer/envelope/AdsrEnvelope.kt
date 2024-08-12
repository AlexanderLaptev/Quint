package lib.quint.synthesizer.envelope

/**
 * An ADSR (attack-decay-sustain-release) envelope commonly used to control
 * volume.
 *
 * @param attack the attack time
 * @param decay the decay time
 * @param sustain the sustain value
 * @param release the release time
 */
class AdsrEnvelope(
    var attack: Double,
    var decay: Double,
    var sustain: Double,
    var release: Double,
) : Envelope {
    override fun getValue(time: Double, duration: Double): Double {
        if (time < 0 || time >= duration) return 0.0
        val releaseTime = duration - release
        return if (time < attack) map(time, 0.0, attack, 0.0, 1.0)
        else if (time < decay) map(time, attack, decay, 1.0, sustain)
        else if (time < releaseTime) sustain
        else map(time, releaseTime, duration, sustain, 0.0)
    }

    private fun map(value: Double, inMin: Double, inMax: Double, outMin: Double, outMax: Double): Double =
        outMin + (value - inMin) / (inMax - inMin) * (outMax - outMin)
}
