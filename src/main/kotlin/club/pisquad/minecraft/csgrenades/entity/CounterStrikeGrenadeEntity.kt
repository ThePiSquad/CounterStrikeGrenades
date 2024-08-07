package club.pisquad.minecraft.csgrenades.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.level.Level

abstract class CounterStrikeGrenadeEntity(pEntityType: EntityType<FlashBangEntity>, pLevel: Level) :
    ThrowableItemProjectile(pEntityType, pLevel)