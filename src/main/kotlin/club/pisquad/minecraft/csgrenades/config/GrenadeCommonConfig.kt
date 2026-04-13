package club.pisquad.minecraft.csgrenades.config

import net.minecraftforge.common.ForgeConfigSpec

class GrenadeCommonConfig(
    val defaultFuseTime: Double = 2.5,
) : ConfigSection {
    lateinit var damageNonPlayer: ForgeConfigSpec.BooleanValue
    lateinit var fuseTime: ForgeConfigSpec.DoubleValue

    override fun build(
        builder: ForgeConfigSpec.Builder,
    ) {
        damageNonPlayer = builder.define("damage_non_player", true)
        fuseTime = builder.defineInRange("fuse_time", defaultFuseTime, 0.1, 60.0)
    }
}