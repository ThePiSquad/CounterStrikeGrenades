package club.pisquad.minecraft.csgrenades.config

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.config.sections.PhysicsConfig
import club.pisquad.minecraft.csgrenades.config.sections.ThrowConfig
import net.minecraftforge.common.ForgeConfigSpec

object ModConfig {
    val SPEC: ForgeConfigSpec
    val physics = PhysicsConfig
    val throwConfig = ThrowConfig

    init {
        val builder = ForgeConfigSpec.Builder()
        builder.comment("Configs for Counter Strike Grenade")

        physics.build(builder)
        throwConfig.build(builder)

        GrenadeType.entries.forEach {
            builder.push(it.resourceKey)
            it.registries.get().config.build(builder)
            builder.pop()
        }


        SPEC = builder.build()
    }
}
