package cute.nahida.hytbot.utils.math

import kotlin.random.Random

object RandomUtils {
    fun random(min: Double, max: Double): Double {
        val actualMin = min.coerceAtMost(max)
        val actualMax = min.coerceAtLeast(max)
        return Random.nextDouble() * (actualMax - actualMin) + actualMin
    }
    fun random(min: Float, max: Float): Float {
        val actualMin = min.coerceAtMost(max)
        val actualMax = min.coerceAtLeast(max)
        return Random.nextFloat() * (actualMax - actualMin) + actualMin
    }
    fun random(min: Int, max: Int): Int {
        val actualMin = min.coerceAtMost(max)
        val actualMax = min.coerceAtLeast(max)
        return Random.nextInt() * (actualMax - actualMin) + actualMin
    }
}