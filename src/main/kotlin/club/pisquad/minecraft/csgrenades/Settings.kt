package club.pisquad.minecraft.csgrenades

const val PLAYER_EYESIGHT_OFFSET = 1.62
const val GRENADE_THROW_COOLDOWN = 20
const val GRENADE_ENTITY_SIZE = 0.3f

// Grenade throw acton settings
// There are two types of throw actions, strong and weak, just like in CSGO
const val STRONG_THROW_SPEED: Double = 1.3
const val WEAK_THROW_SPEED: Double = 0.4
const val STRONG_THROW_PLAYER_SPEED_FACTOR: Double = 1.3
const val WEAK_THROW_PLAYER_SPEED_FACTOR: Double = 0.5

// SMOKE GRENADES
const val SMOKE_GRENADE_RADIUS = 6
const val SMOKE_FUSE_TIME_AFTER_LAND = 1
const val SMOKE_GRENADE_PARTICLE_COUNT = 2000
const val SMOKE_GRENADE_SPREAD_TIME = 0.3
const val SMOKE_GRENADE_TOTAL_GENERATION_TIME = 1.5
const val SMOKE_GRENADE_SMOKE_LIFETIME = 20

// HE GRENADES
const val HEGRENADE_BASE_DAMAGE = 59.0
const val HEGRENADE_DAMAGE_RANGE = 6.5

// Incendiary
const val INCENDIARY_RANGE = 6.0
const val INCENDIARY_LIFETIME = 7.0
const val INCENDIARY_FUSE_TIME = 2.0
const val INCENDIARY_PARTICLE_DENSITY = 2
const val INCENDIARY_PARTICLE_LIFETIME = 30
const val FIRE_EXTINGUISH_RANGE = SMOKE_GRENADE_RADIUS + INCENDIARY_RANGE