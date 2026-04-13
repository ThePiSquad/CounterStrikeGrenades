package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import net.minecraftforge.common.ForgeConfigSpec

object HEGrenadeConfig : GrenadeConfigBuilder {
    val common = GrenadeCommonConfig(2.5)
    val explosion = HEGrenadeExplosionConfig
    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)

        builder.push("explosion")
        explosion.build(builder)
        builder.pop()
    }
}

object HEGrenadeExplosionConfig : GrenadeConfigBuilder {
    lateinit var radius: ForgeConfigSpec.DoubleValue
    lateinit var damageBoostNearHead: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        radius = builder.defineInRange("radius", 2.5, 1.0, 10.0)
        damageBoostNearHead = builder.defineInRange("damage_boost_near_head", 1.5, 0.1, 10.0)
    }
}