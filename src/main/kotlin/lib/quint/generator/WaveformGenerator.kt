package lib.quint.generator

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin

enum class WaveformGenerator : Generator {
    Sine {
        override fun sample(time: Double, frequency: Double, phase: Double): Double =
            sin((PI2 * frequency * time + phase * PI2).mod(PI2))
    },

    Triangle {
        override fun sample(time: Double, frequency: Double, phase: Double): Double {
            val t = (time * frequency + phase).mod(1.0)
            return 4.0 * abs(t- floor(0.5 + t)) - 1.0
        }
    },

    Square {
        override fun sample(time: Double, frequency: Double, phase: Double): Double {
            val t = (time * frequency + phase).mod(1.0)
            return if (t < 0.5) -1.0 else if (t == 0.5) 1.0 else 1.0
        }
    },

    Sawtooth {
        override fun sample(time: Double, frequency: Double, phase: Double): Double {
            val t = (time * frequency + phase).mod(1.0)
            return 2.0 * (t - floor(0.5 + t))
        }
    };

    companion object {
        const val PI2 = 2.0 * PI
    }
}
