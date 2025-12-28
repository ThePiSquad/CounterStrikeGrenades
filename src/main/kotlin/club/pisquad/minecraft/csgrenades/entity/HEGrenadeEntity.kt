package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.client.renderer.HEGrenadeExplosionData
import club.pisquad.minecraft.csgrenades.client.renderer.HEGrenadeRenderManager
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.registery.ModDamageType
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import kotlin.math.max

class HEGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.HEGRENADE) {

    init {
        this.hitBlockSound = ModSoundEvents.HEGRENADE_BOUNCE.get()
    }

    override fun getDefaultItem(): Item {
        return ModItems.HEGRENADE_ITEM.get()
    }

    override fun tick() {
        if (this.entityData.get(isExplodedAccessor)) {
            if (!this.level().isClientSide) { // Server-side removal
                this.discard()
            }
            return // Stop further processing
        }

        super.tick()

        // Explosion logic
        if (getTimeFromTickCount(this.tickCount.toDouble()) > (ModConfig.HEGrenade.FUSE_TIME?.get()
                ?: 2000) / 1000.0
        ) { // isExploded is checked at the beginning
            if (!this.level().isClientSide) { // Server
                this.doDamage()
            } else { // Client
                HEGrenadeRenderManager.render(HEGrenadeExplosionData(this.position()))
                this.blowUpNearbySmokeGrenade()
            }
            this.entityData.set(isExplodedAccessor, true)
        }
    }

    private fun doDamage() {
        val level = this.level() as ServerLevel
        val registryAccess = this.level().registryAccess()
        val damageRange = ModConfig.HEGrenade.DAMAGE_RANGE.get()
        val baseDamageSource = DamageSource(
            registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.HEGRENADE_EXPLOSION),
            this.owner
        )
        val selfDamageSource = DamageSource(
            registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.HEGRENADE_EXPLOSION_SELF)
        )
        val entities =
            level.getEntitiesOfClass(
                if (ModConfig.DAMAGE_NON_PLAYER_ENTITY.get()) LivingEntity::class.java else Player::class.java,
                AABB(this.blockPosition()).inflate(ModConfig.HEGrenade.DAMAGE_RANGE.get())
            )
        for (entity in entities) {
            val distance = entity.distanceTo(this).toDouble()

            if (distance < damageRange) {
                val damage = getDamageBlockingState(entity)
                if (damage > 0.0) {
                    val originalKnockBackResistance =
                        entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue ?: 0.0
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = 1.0

                    if (entity == this.owner) {
                        when (ModConfig.HEGrenade.CAUSE_DAMAGE_TO_OWNER.get()) {
                            ModConfig.SelfDamageSetting.NEVER -> { /* Do nothing */
                            }

                            ModConfig.SelfDamageSetting.NOT_IN_TEAM -> entity.hurt(baseDamageSource, damage.toFloat())
                            ModConfig.SelfDamageSetting.ALWAYS -> entity.hurt(selfDamageSource, damage.toFloat())
                        }
                    } else {
                        entity.hurt(baseDamageSource, damage.toFloat())
                    }

                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = originalKnockBackResistance
                }
            }
        }
    }

    private fun getDamageBlockingState(entity: LivingEntity): Double {
        val headDamage = ClipContext(
            this.position(),
            entity.eyePosition,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.ANY,
            null
        ).let {
            val clipResult = this.level().clip(it)
            return@let if (clipResult.type == HitResult.Type.MISS) {
                val distance = this.position().distanceTo(entity.eyePosition)
                return@let if (distance < 1.5) {
                    calculateHEGrenadeDamage(distance, 0.0, true)
                } else {
                    calculateHEGrenadeDamage(distance, 0.0)
                }
            } else {
                0.0
            }
        }

        val bodyDamage = ClipContext(
            this.position(),
            entity.position(),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.ANY,
            null
        ).let {
            val clipResult = this.level().clip(it)
            return@let if (clipResult.type == HitResult.Type.MISS) {
                calculateHEGrenadeDamage(this.position().distanceTo(entity.position()), 0.0)
            } else {
                0.0
            }
        }

        return max(headDamage, bodyDamage)
    }

    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder =
            registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.HEGRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }

    private fun blowUpNearbySmokeGrenade() {
        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get()
        val heDamageRange = ModConfig.HEGrenade.DAMAGE_RANGE.get()
        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
        this.level().getEntitiesOfClass(
            SmokeGrenadeEntity::class.java,
            this.boundingBox.inflate(heDamageRange + smokeRadius)
                .inflate(0.0, smokeFallingHeight.toDouble(), 0.0)
        ).forEach {
            it.clearSmokeWithinRange(this.position(), heDamageRange + 2.5, true)
        }
    }
}

private fun calculateHEGrenadeDamage(
    distance: Double,
    armorReduction: Double,
    headDamageBoost: Boolean = false
): Double {
    val baseDamage =
        if (headDamageBoost) ModConfig.HEGrenade.BASE_DAMAGE.get() * ModConfig.HEGrenade.HEAD_DAMAGE_BOOST.get() else ModConfig.HEGrenade.BASE_DAMAGE.get()
    val damageRange = ModConfig.HEGrenade.DAMAGE_RANGE.get()
    return baseDamage.times(1.0.minus(distance.div(damageRange)))
        .times(1.0.minus(armorReduction))
}