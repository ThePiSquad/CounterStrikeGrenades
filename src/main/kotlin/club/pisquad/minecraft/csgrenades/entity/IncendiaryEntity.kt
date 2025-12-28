package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registery.ModDamageType
import club.pisquad.minecraft.csgrenades.registery.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class IncendiaryEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    AbstractFireGrenade(pEntityType, pLevel, GrenadeType.INCENDIARY) {
    override fun getDefaultItem(): Item {
        return ModItems.INCENDIARY_ITEM.get()
    }

    override fun getFireDamageType(): ResourceKey<DamageType> {
        return ModDamageType.INCENDIARY_FIRE
    }

    override fun getSelfFireDamageType(): ResourceKey<DamageType> {
        return ModDamageType.INCENDIARY_FIRE_SELF
    }

    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder =
            registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.INCENDIARY_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }
}