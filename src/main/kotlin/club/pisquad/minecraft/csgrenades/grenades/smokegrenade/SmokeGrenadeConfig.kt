package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import net.minecraftforge.common.ForgeConfigSpec


object SmokeGrenadeConfig : GrenadeConfigBuilder {
    val spread = SmokeSpreadConfig

    val common = GrenadeCommonConfig(0.5)

    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)

        builder.push("spread")
        spread.build(builder)
        builder.pop()
    }
}

object SmokeSpreadConfig : GrenadeConfigBuilder {
    lateinit var smokeWidth: ForgeConfigSpec.DoubleValue
    lateinit var smokeHeight: ForgeConfigSpec.DoubleValue
    lateinit var maxFall: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        smokeWidth = builder.defineInRange("shape_max_width", 3.5, 1.0, 10.0)
        smokeHeight = builder.defineInRange("shape_max_height", 3.0, 1.0, 10.0)
        maxFall = builder.defineInRange("shape_max_fall", 10.0, 0.0, 20.0)
    }
}
