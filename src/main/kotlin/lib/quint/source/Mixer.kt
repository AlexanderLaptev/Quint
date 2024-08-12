package lib.quint.source

/**
 * A stereo audio source
 */
class Mixer(
    val slots: MutableList<Slot> = mutableListOf(),
    val masterParams: Params = Params(),
) : StereoAudioSource {
    class Slot(
        val source: StereoAudioSource,
        val params: Params,
    )

    class Params(
        var volume: Double = 1.0,
        var balance: Double = 0.0,
    )

    override fun sampleLeft(time: Double): Double {
        val balanceVolume = if (masterParams.balance <= 0.0) 1.0 else clamp(1.0 - masterParams.balance)
        var sample = 0.0
        for (slot in slots) {
            with(slot) {
                val slotBalanceVolume = if (params.balance <= 0.0) 1.0 else clamp(1.0 - params.balance)
                sample += source.sampleLeft(time) * params.volume * slotBalanceVolume
            }
        }
        return sample / slots.size * balanceVolume * masterParams.volume
    }

    override fun sampleRight(time: Double): Double {
        val balanceVolume = if (masterParams.balance >= 0.0) 1.0 else clamp(1.0 + masterParams.balance)
        var sample = 0.0
        for (slot in slots) {
            with(slot) {
                val slotBalanceVolume = if (params.balance >= 0.0) 1.0 else clamp(1.0 + params.balance)
                sample += source.sampleRight(time) * params.volume * slotBalanceVolume
            }
        }
        return sample / slots.size * balanceVolume * masterParams.volume
    }

    private fun clamp(value: Double): Double =
        if (value > 1.0) 1.0 else if (value < 0.0) 0.0 else value
}
