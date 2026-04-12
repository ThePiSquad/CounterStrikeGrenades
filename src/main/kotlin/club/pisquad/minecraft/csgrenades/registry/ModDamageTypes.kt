package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageType

interface GrenadeEntityDamageTypes {
    val hit: ResourceKey<DamageType>
    val main: ResourceKey<DamageType>
}

object ModDamageTypes {
    fun registerSingle(path: String): ResourceKey<DamageType> {
        return ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation(
                CounterStrikeGrenades.ID, path,
            ),
        )
    }
}