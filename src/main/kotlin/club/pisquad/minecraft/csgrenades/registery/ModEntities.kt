package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GRENADE_ENTITY_SIZE
import club.pisquad.minecraft.csgrenades.entity.FlashBangEntity
import club.pisquad.minecraft.csgrenades.entity.SmokeGrenadeEntity
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModEntities {
    val ENTITIES: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CounterStrikeGrenades.ID)

    val FLASH_BANG_ENTITY: RegistryObject<EntityType<FlashBangEntity>> = ENTITIES.register("flashbang") {
        EntityType.Builder.of(
            { pEntityType: EntityType<FlashBangEntity>, pLevel: Level -> FlashBangEntity(pEntityType, pLevel) },
            MobCategory.MISC
        ).sized(GRENADE_ENTITY_SIZE, GRENADE_ENTITY_SIZE)
            .build(ResourceLocation(CounterStrikeGrenades.ID, "flashbang").toString())
    }

    val SMOKE_GRENADE_ENTITY: RegistryObject<EntityType<SmokeGrenadeEntity>> = ENTITIES.register("smokegrenade") {
        EntityType.Builder.of(
            { pEntityType: EntityType<SmokeGrenadeEntity>, pLevel: Level -> SmokeGrenadeEntity(pEntityType, pLevel) },
            MobCategory.MISC
        ).sized(GRENADE_ENTITY_SIZE, GRENADE_ENTITY_SIZE)
            .build(ResourceLocation(CounterStrikeGrenades.ID, "smokegrenade").toString())
    }

}