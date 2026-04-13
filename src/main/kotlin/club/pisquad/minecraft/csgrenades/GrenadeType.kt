package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovRegistries
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistries
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistries
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import java.util.function.Supplier

interface WithGrenadeType {
    val grenadeType: GrenadeType
}

enum class GrenadeType(
    val resourceKey: String,
    // Use supplier to avoid circular initialization
    val registries: Supplier<CounterStrikeGrenadeRegistries<out CounterStrikeGrenadeEntity, out CounterStrikeGrenadeItem, out GrenadeEntityDamageTypes, out GrenadeSoundEvents, out GrenadeConfigBuilder>>,
) {
    FLASH_BANG(
        "flashbang",
        { FlashbangRegistries },
    ),
    SMOKE_GRENADE(
        "smokegrenade",
        { SmokeGrenadeRegistries },
    ),
    HE_GRENADE(
        "hegrenade",
        { HEGrenadeRegistries }
    ),
    INCENDIARY(
        "incendiary",
        { IncendiaryRegistries },
    ),
    MOLOTOV(
        "molotov",
        { MolotovRegistries },
    ),
    DECOY(
        "decoy",
        { DecoyRegistries }
    ),
}

enum class ThrowType(
    val getSpeed: () -> Double
) {
    WEAK({ ModConfig.throwConfig.speed_weak.get().toMetersPerTick() }),
    MEDIUM({ ModConfig.throwConfig.speed_medium.get().toMetersPerTick() }),
    STRONG({ ModConfig.throwConfig.speed_strong.get().toMetersPerTick() }),
}