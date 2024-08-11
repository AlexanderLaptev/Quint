package lib.quint.util

import java.util.concurrent.CountDownLatch
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

fun Clip.playBlocking() {
    val latch = CountDownLatch(1)

    this.addLineListener {
        if (it.type == LineEvent.Type.STOP) latch.countDown()
    }

    start()
    latch.await()
}
