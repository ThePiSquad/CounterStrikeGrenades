package club.pisquad.minecraft.csgrenades.core

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import net.minecraft.world.entity.EntityType
import net.minecraftforge.registries.RegistryObject

abstract class CounterStrikeGrenadeRegistries<
        E : CounterStrikeGrenadeEntity,
        I : CounterStrikeGrenadeItem,
        D : GrenadeEntityDamageTypes,
        S : GrenadeSoundEvents,
        C : GrenadeConfigBuilder
        >(
    val grenadeType: GrenadeType,
    val damageTypes: D,
    val sounds: S,
    val config: C,
    entityFactory: EntityType.EntityFactory<E>,
    itemFactory: () -> I
) {
    val entity: RegistryObject<EntityType<E>> = ModEntities.registerSingle(grenadeType.resourceKey, entityFactory)
    val item: RegistryObject<I> = ModItems.registerSingle(grenadeType.resourceKey, itemFactory)

    open fun registerSerializers() {}
}