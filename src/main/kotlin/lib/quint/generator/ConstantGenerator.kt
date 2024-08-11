package lib.quint.generator

class ConstantGenerator(
    var constant: Double = 0.0
) : Generator {
    override fun sample(time: Double, frequency: Double, phase: Double): Double = constant
}
