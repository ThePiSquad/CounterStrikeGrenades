package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.FIRE_EXTINGUISH_RANGE
import club.pisquad.minecraft.csgrenades.SMOKE_FUSE_TIME_AFTER_LAND
import club.pisquad.minecraft.csgrenades.SMOKE_GRENADE_SMOKE_LIFETIME
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getFireExtinguishRange
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.SmokeEmittedMessage
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.toVec3i
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.PacketDistributor

class SmokeGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.FLASH_BANG) {

    private var lastPos: Vec3i = Vec3i(0, 0, 0)
    private var tickCount: Int = 0

    override fun getDefaultItem(): Item {
        return ModItems.SMOKE_GRENADE_ITEM.get()
    }

    override fun tick() {
        super.tick()
        if (this.level().isClientSide) return
        // Smoke grenade's fuse time is 1 after landing,
        // we detect if the smoke grenade has moved during the last 1.2 second
        if (super.isLanded && !isExploded) {
            val currentPos = this.position().toVec3i()
            if (currentPos == lastPos) {
                tickCount++
            } else {
                tickCount = 0
                this.lastPos = currentPos
            }

            if (getTimeFromTickCount(tickCount.toDouble()) > SMOKE_FUSE_TIME_AFTER_LAND) {
                CsGrenadePacketHandler.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    SmokeEmittedMessage(this.id, this.position())
                )
                this.isExploded = true
                tickCount = 0
            }
        }
        if (this.isExploded) {
            tickCount++
            if (getTimeFromTickCount(tickCount.toDouble()) > SMOKE_GRENADE_SMOKE_LIFETIME) {
                this.kill()
            }
            // Extinguish nearby fires
            extinguishNearbyFires()
        }
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        lastPos = this.position().toVec3i()
    }

    override fun onHitBlock(result: BlockHitResult) {
        // If the smoke hit the ground with in any incendiary's range, it will emit right away
        if (result.direction == Direction.UP) {
            if (extinguishNearbyFires() > 0) {
                this.isLanded = true
            }
        }


        super.onHitBlock(result)
    }

    private fun extinguishNearbyFires(): Int {
        val bb = getFireExtinguishRange(this.position())

        val extinguishedFires = this.level().getEntitiesOfClass(
            IncendiaryEntity::class.java,
            bb
        ) { this.position().distanceTo(it.position()) < FIRE_EXTINGUISH_RANGE && it.isExploded }

        extinguishedFires.forEach { it.extinguish() }
        return extinguishedFires.size

    }
}

