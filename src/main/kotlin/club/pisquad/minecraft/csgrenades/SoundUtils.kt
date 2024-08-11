package club.pisquad.minecraft.csgrenades

import kotlin.math.max

enum class SoundTypes(val initialVolume: Double, val hearableRange: Double) {
    // General sound types
    GRENADE_HIT(0.09, 30.0),

    // Flashbang
    FLASHBANG_EXPLODE(0.08, 80.0),
    FLASHBANG_EXPLODE_DISTANT(0.5, 80.0),
    FLASHBANG_RING(0.03, 80.0),

    // Smoke grenades
    SMOKE_GRENADE_EMIT(0.1, 30.0),
    SMOKE_GRENADE_EXPLODE_DISTANT(0.03, 80.0),

    // HEGrenades
    HEGRENADE_EXPLODE(0.2, 30.0),
    HEGRENADE_EXPLODE_DISTANT(1.0, 80.0),


}

object SoundUtils {
    fun getVolumeFromDistance(distance: Double, soundType: SoundTypes): Double {
        return max(0.0, soundType.initialVolume.times(1 - distance.div(soundType.hearableRange)))
    }
}

