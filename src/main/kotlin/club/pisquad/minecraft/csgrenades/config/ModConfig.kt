package club.pisquad.minecraft.csgrenades.config

import club.pisquad.minecraft.csgrenades.config.sections.*
import net.minecraftforge.common.ForgeConfigSpec

object ModConfig {
    val SPEC: ForgeConfigSpec
    val physics = PhysicsConfig
    val throwConfig = ThrowConfig
    val hegrenade = HEGrenadeConfig
    val flashbang = FlashBangConfig
    val smokegrenade = SmokeGrenadeConfig
    val incendiary = IncendiaryConfig
    val molotov = MolotovConfig
    val decoy = DecoyConfig
//
//
//    //    var FOV_EFFECT_AMOUNT: ForgeConfigSpec.DoubleValue
////    var DAMAGE_NON_PLAYER_ENTITY: ForgeConfigSpec.BooleanValue
////    var TRAJECTORY_PREVIEW_COLOR: ForgeConfigSpec.ConfigValue<String>
//
//    object SmokeGrenade {
//        lateinit var SMOKE_RADIUS: ForgeConfigSpec.IntValue
//        lateinit var FUSE_TIME_AFTER_LANDING: ForgeConfigSpec.DoubleValue
//        lateinit var SMOKE_LIFETIME: ForgeConfigSpec.LongValue
//        lateinit var TIME_BEFORE_REGENERATE: ForgeConfigSpec.LongValue
//        lateinit var REGENERATION_TIME: ForgeConfigSpec.LongValue
//        lateinit var SMOKE_MAX_FALLING_HEIGHT: ForgeConfigSpec.IntValue
//        lateinit var ARROW_CLEAR_RANGE: ForgeConfigSpec.DoubleValue
//        lateinit var BULLET_CLEAR_RANGE: ForgeConfigSpec.DoubleValue
//    }
//
//    object HEGrenade {
//        lateinit var FUSE_TIME: ForgeConfigSpec.DoubleValue
//        lateinit var BASE_DAMAGE: ForgeConfigSpec.DoubleValue
//        lateinit var DAMAGE_RADIUS: ForgeConfigSpec.DoubleValue
//        lateinit var HEAD_DAMAGE_BOOST: ForgeConfigSpec.DoubleValue
//        lateinit var CAUSE_DAMAGE_TO_OWNER: ForgeConfigSpec.EnumValue<SelfDamageSetting>
//    }
//
//    object FireGrenade {
//        lateinit var FIRE_RANGE: ForgeConfigSpec.IntValue
//        lateinit var LIFETIME: ForgeConfigSpec.LongValue
//        lateinit var FUSE_TIME: ForgeConfigSpec.DoubleValue
//        lateinit var FIRE_EXTINGUISH_RANGE: ForgeConfigSpec.IntValue
//        lateinit var FIRE_MAX_SPREAD_DOWNWARD: ForgeConfigSpec.IntValue
//        lateinit var DAMAGE: ForgeConfigSpec.DoubleValue
//        lateinit var DAMAGE_INCREASE_TIME: ForgeConfigSpec.LongValue
//        lateinit var CAUSE_DAMAGE_TO_OWNER: ForgeConfigSpec.EnumValue<SelfDamageSetting>
//    }
//
//    object Flashbang {
//        lateinit var EFFECTIVE_RANGE: ForgeConfigSpec.DoubleValue
//        lateinit var FUSE_TIME: ForgeConfigSpec.LongValue
//        lateinit var MAX_DURATION: ForgeConfigSpec.DoubleValue
//        lateinit var MIN_DURATION: ForgeConfigSpec.DoubleValue
//        lateinit var DISTANCE_DECAY_EXPONENT: ForgeConfigSpec.DoubleValue
//    }
//
//    object Decoy {
//        lateinit var FUSE_TIME_AFTER_LANDING: ForgeConfigSpec.DoubleValue
//    }

//    enum class SelfDamageSetting {
//        NEVER,
//        NOT_IN_TEAM,
//        ALWAYS,
//    }

    init {
        val builder = ForgeConfigSpec.Builder()
        builder.comment("Configs for Counter Strike Grenade")

        physics.build(builder)
        throwConfig.build(builder)
        hegrenade.build(builder)
        flashbang.build(builder)
        smokegrenade.build(builder)
        incendiary.build(builder)
        decoy.build(builder)
        molotov.build(builder)


        SPEC = builder.build()
    }
}
