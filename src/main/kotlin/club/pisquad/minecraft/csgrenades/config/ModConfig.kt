package club.pisquad.minecraft.csgrenades.config

import net.minecraftforge.common.ForgeConfigSpec

object ModConfig {
    val SPEC: ForgeConfigSpec

    var IGNORE_BARRIER_BLOCK: ForgeConfigSpec.BooleanValue
    var GRENADE_THROW_COOLDOWN: ForgeConfigSpec.IntValue
    var THROW_SPEED_STRONG: ForgeConfigSpec.DoubleValue
    var THROW_SPEED_WEAK: ForgeConfigSpec.DoubleValue
    var THROW_SPEED_MODERATE: ForgeConfigSpec.DoubleValue
    var PLAYER_SPEED_FACTOR_STRONG: ForgeConfigSpec.DoubleValue
    var PLAYER_SPEED_FACTOR_WEAK: ForgeConfigSpec.DoubleValue
    var THROW_TYPE_TRANSIENT_TIME: ForgeConfigSpec.IntValue
    var FOV_EFFECT_AMOUNT: ForgeConfigSpec.DoubleValue
    var DAMAGE_NON_PLAYER_ENTITY: ForgeConfigSpec.BooleanValue

    object SmokeGrenade {
        lateinit var SMOKE_RADIUS: ForgeConfigSpec.IntValue
        lateinit var FUSE_TIME_AFTER_LANDING: ForgeConfigSpec.LongValue
        lateinit var SMOKE_LIFETIME: ForgeConfigSpec.LongValue
        lateinit var TIME_BEFORE_REGENERATE: ForgeConfigSpec.LongValue
        lateinit var REGENERATION_TIME: ForgeConfigSpec.LongValue
        lateinit var SMOKE_MAX_FALLING_HEIGHT: ForgeConfigSpec.IntValue
        lateinit var ARROW_CLEAR_RANGE: ForgeConfigSpec.DoubleValue
        lateinit var BULLET_CLEAR_RANGE: ForgeConfigSpec.DoubleValue
    }

    object HEGrenade {
        var FUSE_TIME: ForgeConfigSpec.LongValue? = null
        lateinit var BASE_DAMAGE: ForgeConfigSpec.DoubleValue
        lateinit var DAMAGE_RANGE: ForgeConfigSpec.DoubleValue
        lateinit var HEAD_DAMAGE_BOOST: ForgeConfigSpec.DoubleValue
        lateinit var CAUSE_DAMAGE_TO_OWNER: ForgeConfigSpec.EnumValue<SelfDamageSetting>
    }

    object FireGrenade {
        lateinit var FIRE_RANGE: ForgeConfigSpec.IntValue
        lateinit var LIFETIME: ForgeConfigSpec.LongValue
        lateinit var FUSE_TIME: ForgeConfigSpec.LongValue
        lateinit var FIRE_EXTINGUISH_RANGE: ForgeConfigSpec.IntValue
        lateinit var FIRE_MAX_SPREAD_DOWNWARD: ForgeConfigSpec.IntValue
        lateinit var DAMAGE: ForgeConfigSpec.DoubleValue
        lateinit var DAMAGE_INCREASE_TIME: ForgeConfigSpec.LongValue
        lateinit var CAUSE_DAMAGE_TO_OWNER: ForgeConfigSpec.EnumValue<SelfDamageSetting>
    }

    object Flashbang {
        lateinit var EFFECTIVE_RANGE: ForgeConfigSpec.DoubleValue
        lateinit var FUSE_TIME: ForgeConfigSpec.LongValue
        lateinit var MAX_DURATION: ForgeConfigSpec.DoubleValue
        lateinit var MIN_DURATION: ForgeConfigSpec.DoubleValue
        lateinit var DISTANCE_DECAY_EXPONENT: ForgeConfigSpec.DoubleValue
    }

    enum class SelfDamageSetting {
        NEVER, NOT_IN_TEAM, ALWAYS
    }

    init {
        val builder = ForgeConfigSpec.Builder()
        builder.comment("Configs for Counter Strike Grenade")
        builder.comment("Configs are separated into different scopes based on the type of grenade")

//      Common configs
        builder.comment("Should grenade entities fly through barrier block?")
        IGNORE_BARRIER_BLOCK = builder.define("ignore_barrier_block", false)
//      GRENADE_ENTITY_SIZE = builder.defineInRange("grenade_entity_size", 0.3, 0.1, 10.0)
        builder.comment("Throw cooldown, in milliseconds")
        GRENADE_THROW_COOLDOWN = builder.defineInRange("grenade_throw_cooldown", 1000, 0, 60 * 1000)
        builder.comment("Throw speed when using primary button (left click by default)")
        THROW_SPEED_STRONG = builder.defineInRange("throw_speed_strong", 1.3, 0.0, 10.0)
        builder.comment("Throw speed when using secondary button (right click by default)")
        THROW_SPEED_WEAK = builder.defineInRange("throw_speed_weak", 0.4, 0.0, 10.0)
        builder.comment("Throw speed when using holding both button at the same time")
        THROW_SPEED_MODERATE = builder.defineInRange("throw_speed_moderate", 1.0, 0.0, 10.0)
        PLAYER_SPEED_FACTOR_STRONG = builder.defineInRange("player_speed_factor_strong", 1.3, 0.0, 10.0)
        PLAYER_SPEED_FACTOR_WEAK = builder.defineInRange("player_speed_factor_weak", 0.5, 0.0, 10.0)
        builder.comment("Transient time for throw type, in milliseconds")
        THROW_TYPE_TRANSIENT_TIME = builder.defineInRange("throw_type_transient_time", 1000, 0, 60 * 1000)
        FOV_EFFECT_AMOUNT = builder.defineInRange("fov_effect_amount", 0.12, 0.0, 1.0)
        builder.comment("Damage living entities other than player")
        DAMAGE_NON_PLAYER_ENTITY = builder.define("damage_non_player_entity", true)

        builder.push("SmokeGrenade")
        builder.comment("Smoke radius, in block")
        SmokeGrenade.SMOKE_RADIUS = builder.defineInRange("smoke_radius", 6, 2, 10)
        SmokeGrenade.FUSE_TIME_AFTER_LANDING =
            builder.defineInRange("fuse_time_after_landing", 500, 0, 10 * 1000.toLong())
        SmokeGrenade.SMOKE_LIFETIME = builder.defineInRange("smoke_lifetime", 20000, 0, 60 * 1000.toLong())
        SmokeGrenade.TIME_BEFORE_REGENERATE = builder.defineInRange("time_before_regenerate", 1000, 0, 10000.toLong())
        SmokeGrenade.REGENERATION_TIME = builder.defineInRange("regeneration_time", 3000, 0, 10000.toLong())
        SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT = builder.defineInRange("smoke_max_falling_height", 8, 0, 100)
        builder.comment("The radius of smoke cleared by a passing arrow, in blocks.")
        SmokeGrenade.ARROW_CLEAR_RANGE = builder.defineInRange("arrow_clear_range", 1.2, 0.1, 10.0)
        builder.comment("The radius of smoke cleared by a passing bullet (e.g. from Tacz), in blocks.")
        SmokeGrenade.BULLET_CLEAR_RANGE = builder.defineInRange("bullet_clear_range", 1.0, 0.1, 10.0)
        builder.pop()

        builder.push("HEGrenade")
        builder.comment("HE grenade's damage follow a linear decay function")
        HEGrenade.BASE_DAMAGE = builder.defineInRange("base_damage", 30.0, 0.0, 100.0)
        HEGrenade.DAMAGE_RANGE = builder.defineInRange("damage_range", 5.0, 0.0, 100.0)
        HEGrenade.HEAD_DAMAGE_BOOST = builder.defineInRange("head_damage_boost", 1.5, 0.0, 100.0)
        builder.comment("Fuse time before explosion, in milliseconds")
        HEGrenade.FUSE_TIME = builder.defineInRange("fuseTime", 2000L, 0L, 10000L)
        HEGrenade.CAUSE_DAMAGE_TO_OWNER =
            builder.defineEnum("causeDamageToOwner", SelfDamageSetting.ALWAYS, SelfDamageSetting.entries)
        builder.pop()

        builder.push("FireGrenade")
        FireGrenade.FIRE_RANGE = builder.defineInRange("fire_range", 6, 0, 100)
        builder.comment("Lifetime of the fire, in milliseconds")
        FireGrenade.LIFETIME = builder.defineInRange("lifetime", 7000, 0, 100 * 1000.toLong())
        builder.comment("Fuse time before air explode, in milliseconds")
        FireGrenade.FUSE_TIME = builder.defineInRange("fuse_time", 2000.toLong(), 0, 100 * 1000.toLong())
        FireGrenade.FIRE_EXTINGUISH_RANGE = builder.defineInRange("fire_extinguish_range", 6, 0, 100)
        FireGrenade.FIRE_MAX_SPREAD_DOWNWARD = builder.defineInRange("fire_max_spread_downward", 10, 0, 100)
        FireGrenade.DAMAGE = builder.defineInRange("damage", 3.0, 0.0, 100.0)
        builder.comment("In what time should fire damage reach its maximum damage (linearly)")
        FireGrenade.DAMAGE_INCREASE_TIME = builder.defineInRange("damage_increase_time", 2000, 0, 100 * 1000.toLong())
        FireGrenade.CAUSE_DAMAGE_TO_OWNER =
            builder.defineEnum("causeDamageToOwner", SelfDamageSetting.ALWAYS, SelfDamageSetting.entries)
        builder.pop() // Correctly close FireGrenade section

        builder.push("Flashbang")
        builder.comment("The maximum distance at which the flashbang has a significant effect.")
        Flashbang.EFFECTIVE_RANGE = builder.defineInRange("effectiveRange", 64.0, 1.0, 256.0)
        builder.comment("Fuse time from throw to detonation, in milliseconds.")
        Flashbang.FUSE_TIME = builder.defineInRange("fuseTime", 1600L, 0L, 10000L)
        builder.comment("Maximum total blindness duration (at point-blank, direct view), in seconds.")
        Flashbang.MAX_DURATION = builder.defineInRange("maxDuration", 5.0, 0.0, 30.0)
        builder.comment("Minimum total blindness duration (when fully facing away), in seconds.")
        Flashbang.MIN_DURATION = builder.defineInRange("minDuration", 0.25, 0.0, 10.0)
        builder.comment("Controls the curve of how the effect fades with distance. 1.0 is linear, >1.0 is steeper falloff at range (stronger close up).")
        Flashbang.DISTANCE_DECAY_EXPONENT = builder.defineInRange("distanceDecayExponent", 2.0, 0.5, 5.0)
        builder.pop()

        SPEC = builder.build()
    }
}