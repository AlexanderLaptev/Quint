package lib.quint.util

import java.util.concurrent.CountDownLatch
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

/**
 * Plays this clip in its entirety, blocking the current thread until the
 * playback stops.
 */
fun Clip.playBlocking() {
    val latch = CountDownLatch(1)

    this.addLineListener {
        if (it.type == LineEvent.Type.STOP) latch.countDown()
    }

    start()
    latch.await()
}
