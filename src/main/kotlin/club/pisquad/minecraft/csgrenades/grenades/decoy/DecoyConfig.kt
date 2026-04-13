package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import net.minecraftforge.common.ForgeConfigSpec

object DecoyConfig : GrenadeConfigBuilder {
    val common = GrenadeCommonConfig()
    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)
    }
}