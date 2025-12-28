package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageType

object ModDamageType {
    val HEGRENADE_EXPLOSION: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "hegrenade_explosion"))
    val HEGRENADE_EXPLOSION_SELF: ResourceKey<DamageType> =
        ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation(CounterStrikeGrenades.ID, "hegrenade_explosion_self")
        )
    val HEGRENADE_HIT: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "hegrenade_hit"))
    val INCENDIARY_FIRE: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "incendiary_fire"))
    val INCENDIARY_FIRE_SELF: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "incendiary_fire_self"))
    val INCENDIARY_HIT: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "incendiary_hit"))
    val MOLOTOV_FIRE: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "molotov_fire"))
    val MOLOTOV_FIRE_SELF: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "molotov_fire_self"))
    val MOLOTOV_HIT: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "molotov_hit"))
    val FLASHBANG_HIT: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "flashbang_hit"))
    val SMOKEGRENADE_HIT: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "smokegrenade_hit"))
    val DECOY_GRENADE_HIT: ResourceKey<DamageType> =
        ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation(CounterStrikeGrenades.ID, "decoy_hit"))
}