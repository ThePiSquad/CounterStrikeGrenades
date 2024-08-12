package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.INCENDIARY_LIFETIME
import club.pisquad.minecraft.csgrenades.INCENDIARY_RANGE
import club.pisquad.minecraft.csgrenades.damagesource.IncendiaryDamageSource
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.IncendiaryExplodedMessage
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor
import kotlin.math.acos

class IncendiaryEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.INCENDIARY) {

    var explosionTick = 0

    init {
        hitBlockSound = ModSoundEvents.HEGRENADE_BOUNCE.get()
    }

    override fun getDefaultItem(): Item {
        return ModItems.INCENDIARY_ITEM.get()
    }

    override fun tick() {
        super.tick()
        if (this.level() is ClientLevel) return
        if (this.isExploded) {
            // Damage players within range

            val level = this.level() as ServerLevel
            for (player in level.players()) {
                val distance = player.distanceTo(this).toDouble()
                if (distance < INCENDIARY_RANGE) {
                    // workaround the invulnerable time
                    // Notice
                    player.hurt(IncendiaryDamageSource(), 0.6f)
                    player.invulnerableTime = 0
                }
            }

            if (getTimeFromTickCount((this.tickCount - this.explosionTick).toDouble()) > INCENDIARY_LIFETIME) {
                this.kill()
                return
            }
        }
    }

    override fun onHitBlock(result: BlockHitResult) {
        super.onHitBlock(result)

        // Incendiary Grenade Explodes when hit a walkable surface from 30 or smaller angle.
        // val horizontalSpeedDirection = Vec3(this.deltaMovement.x, this.deltaMovement.z).normalized()

        if (result.direction == Direction.UP) {
            val horizontalSpeed = Vec3(this.deltaMovement.x, 0.0, this.deltaMovement.z)
            val angle = acos(horizontalSpeed.normalize().dot(this.deltaMovement.normalize()))
            if (angle < 30.0) {
                this.isLanded = true
                this.isExploded = true
                this.deltaMovement = Vec3.ZERO

                this.explosionTick = this.tickCount

                sendExplodedMessage()
            }
        }
    }

    fun sendExplodedMessage() {
        CsGrenadePacketHandler.INSTANCE.send(
            PacketDistributor.ALL.noArg(),
            IncendiaryExplodedMessage(this.id, this.position())
        )
    }

}