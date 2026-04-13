package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import net.minecraftforge.common.ForgeConfigSpec

class FireGrenadeCommonConfig(
    val defaultSpreadRadius: Double = 3.0,
    val defaultMaxDamagePerTick: Double = 2.0,
) : ConfigSection {
    lateinit var spreadRadius: ForgeConfigSpec.DoubleValue
    lateinit var maxDamagePerTick: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.defineInRange("spread_radius", defaultSpreadRadius, 1.0, 10.0)
        builder.defineInRange("max_damage_per_tick", defaultMaxDamagePerTick, 0.0, 10.0)
    }
}