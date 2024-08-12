package lib.quint.generator

import kotlin.random.Random

/**
 * A generator of white noise (equally distributed frequencies).
 *
 * @param random the random object to use for generating the noise
 */
class WhiteNoiseGenerator(
    var random: Random = Random,
) : Generator {
    override fun sample(time: Double, frequency: Double, phase: Double): Double =
        -1.0f + 2.0f * random.nextDouble()
}
