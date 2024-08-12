package lib.quint.generator

/**
 * A generator always outputting a constant value.
 *
 * @param constant the constant this generator will always output
 */
class ConstantGenerator(
    var constant: Double = 0.0,
) : Generator {
    override fun sample(time: Double, frequency: Double, phase: Double): Double = constant
}
