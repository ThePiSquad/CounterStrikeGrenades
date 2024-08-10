package club.pisquad.minecraft.csgrenades

import kotlin.math.max

enum class SoundTypes(val initialVolume: Double, val hearableRange: Double) {
    // General sound types
    GRENADE_HIT(0.2, 30.0),

    // Flashbang
    FLASHBANG_EXPLODE(0.25, 50.0)

}

object SoundUtils {
    fun getVolumeFromDistance(distance: Double, soundType: SoundTypes): Double {
        return max(0.0, soundType.initialVolume.times(1 - distance.div(soundType.hearableRange)))
    }
}

