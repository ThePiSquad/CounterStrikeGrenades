package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeCommonConfig
import net.minecraftforge.common.ForgeConfigSpec

object MolotovConfig : GrenadeConfigBuilder {
    val common = GrenadeCommonConfig(2.5)
    val firegrenade = FireGrenadeCommonConfig()
    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)
        firegrenade.build(builder)
    }
}
