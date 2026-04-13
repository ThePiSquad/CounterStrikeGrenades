package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.ModSettings
import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import net.minecraftforge.common.ForgeConfigSpec

object FlashBangConfig : GrenadeConfigBuilder {
    val common = GrenadeCommonConfig(1.4)
    val blindEffect = FlashBangBlindEffectConfig
    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)
        builder.push("blindEffect")
        blindEffect.build(builder)
        builder.pop()
    }
}

object FlashBangBlindEffectConfig : GrenadeConfigBuilder {
    lateinit var ranges: ForgeConfigSpec.ConfigValue<List<Double>>
    lateinit var fadingRange: ForgeConfigSpec.DoubleValue
    lateinit var maxRadius: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.comment("Defined as a list of triples in following format: [Angle from player's aim(degrees), Time of full blindness, Total time of blinding effects]")
        builder.comment("Angles from two adjacent triples define a range")
        builder.comment("See cs:go's data: https://counterstrike.fandom.com/wiki/Flashbang")
        ranges = builder.defineList(
            "ranges", listOf(
                53.0, 1.88, 4.87,
                72.0, 0.45, 3.4,
                101.0, 0.08, 1.95,
                180.0, 0.08, 0.95,
            )
        ) { it is Double && it < 181 }
        fadingRange =
            builder.defineInRange("fading_range", 50.0, 1.0, ModSettings.SERVER_MESSAGE_RANGE)

        maxRadius =
            builder.defineInRange("max_radius", 50.0, 10.0, ModSettings.SERVER_MESSAGE_RANGE)

    }
}