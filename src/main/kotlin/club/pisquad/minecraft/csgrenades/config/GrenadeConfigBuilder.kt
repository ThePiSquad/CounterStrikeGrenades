package club.pisquad.minecraft.csgrenades.config

import net.minecraftforge.common.ForgeConfigSpec

interface GrenadeConfigBuilder {
    fun build(builder: ForgeConfigSpec.Builder)
}