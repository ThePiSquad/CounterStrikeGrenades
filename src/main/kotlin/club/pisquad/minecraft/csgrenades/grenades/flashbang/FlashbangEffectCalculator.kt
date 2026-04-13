package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.isBetween
import club.pisquad.minecraft.csgrenades.minus
import kotlinx.serialization.Serializable
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.PI
import kotlin.math.acos

object FlashbangEffectCalculator {
    private var effectRangesCache: List<EffectRange>? = null

    fun calculate(grenadePos: Vec3, eyePos: Vec3, lookAtAngle: Vec3): FlashbangBlindEffectData? {
        val v = grenadePos.minus(eyePos)
        val direction = v.normalize()
        val distance = v.length()

        val angle = acos(lookAtAngle.normalize().dot(direction)).times(180.0).div(PI)

        val effectRanges = getEffectRangesFromConfig()

        for (range in effectRanges) {
            if (angle.isBetween(range.angleMin, range.angleMax)) {
                val f = Mth.clamp(distance.div(FlashBangConfig.blindEffect.fadingRange.get()), 0.0, 1.0)
                val fullBlindDuration = Mth.lerp(f, range.fullBlindDuration, 0.0)
                val totalDuration = Mth.lerp(f, range.totalDuration, 0.0)
                return FlashbangBlindEffectData(fullBlindDuration, totalDuration)
            }
        }

        return null
    }

    private fun getEffectRangesFromConfig(): List<EffectRange> {
        if (effectRangesCache != null) {
            return effectRangesCache!!
        }

        val ranges = FlashBangConfig.blindEffect.ranges.get()

        var rangeMin: Double = 0.0
        var rangeMax: Double = ranges[0]
        var fullBlindDuration: Double = ranges[1]

        val result: MutableList<EffectRange> = mutableListOf()

        ranges.forEachIndexed { index, d ->
            when (index.mod(3)) {
                0 -> {
                    rangeMax = d
                }

                1 -> {
                    fullBlindDuration = d
                }

                2 -> {
                    result.add(
                        EffectRange(
                            rangeMin,
                            rangeMax,
                            fullBlindDuration,
                            d
                        )
                    )
                    rangeMin = rangeMax
                }

                else -> {
                    throw Exception("This should never happen")
                }
            }
        }
        effectRangesCache = result
        return result
    }

    private data class EffectRange(
        val angleMin: Double,
        val angleMax: Double,
        val fullBlindDuration: Double,
        val totalDuration: Double
    )
}

@Serializable
data class FlashbangBlindEffectData(
    val fullBlindDuration: Double,
    val totalDuration: Double,
)