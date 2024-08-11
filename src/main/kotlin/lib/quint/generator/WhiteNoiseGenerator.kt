package lib.quint.generator

import kotlin.random.Random

class WhiteNoiseGenerator(
    var random: Random = Random,
) : Generator {
    override fun sample(time: Double, frequency: Double, phase: Double): Double =
        -1.0f + 2.0f * random.nextDouble()
}
