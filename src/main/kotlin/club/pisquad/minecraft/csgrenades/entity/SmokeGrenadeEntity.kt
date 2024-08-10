package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.SmokeEmittedMessage
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.toVec3i
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.network.PacketDistributor

class SmokeGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.FLASH_BANG) {

    private var lastPos: Vec3i = Vec3i(0, 0, 0)
    private var tickCount: Int = 0
    private var isEmitted = false

    override fun getDefaultItem(): Item {
        return ModItems.SMOKE_GRENADE_ITEM.get()
    }

    override fun tick() {
        super.tick()
        if (this.level().isClientSide) return
        // Smoke grenade's fuse time is 1.2 after landing,
        // we detect if the smoke grenade has moved during the last 1.2 second
        if (super.isLanded && !isEmitted) {
            val currentPos = this.position().toVec3i()
            if (currentPos == lastPos) {
                tickCount++
            } else {
                tickCount = 0
                this.lastPos = currentPos
            }

            if (getTimeFromTickCount(tickCount.toDouble()) > 1.2) {
                CsGrenadePacketHandler.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    SmokeEmittedMessage(this.id, this.position())
                )
                this.isEmitted = true
                tickCount = 0
            }
        }
        if (this.isEmitted) {
            tickCount++
            if (getTimeFromTickCount(tickCount.toDouble()) > 18.0) {
                this.kill()
            }
        }
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        lastPos = this.position().toVec3i()
    }
}