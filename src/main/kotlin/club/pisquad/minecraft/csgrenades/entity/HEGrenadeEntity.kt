package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.HEGRENADE_BASE_DAMAGE
import club.pisquad.minecraft.csgrenades.HEGRENADE_DAMAGE_RANGE
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.HEGrenadeExplodedMessage
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.network.PacketDistributor

class HEGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.FLASH_BANG) {

    init {
        this.hitBlockSound = ModSoundEvents.HEGRENADE_BOUNCE.get()
    }

    override fun getDefaultItem(): Item {
        return ModItems.HEGRENADE_ITEM.get()
    }

    override fun tick() {
        super.tick()

        if (this.level() is ClientLevel) return
        if (getTimeFromTickCount(this.tickCount.toDouble()) > 2.5) {
            val level = this.level() as ServerLevel
            for (player in level.players()) {
                val distance = player.distanceTo(this).toDouble()
                if (distance < HEGRENADE_DAMAGE_RANGE) {
                    player.hurt(player.damageSources().generic(), calculateHEGrenadeDamage(distance, 0.0).toFloat())
                }
            }
            CsGrenadePacketHandler.INSTANCE.send(
                PacketDistributor.ALL.noArg(),
                HEGrenadeExplodedMessage(
                    this.id,
                    this.position(),
                )
            )
            this.kill()
        }
    }
}

private fun calculateHEGrenadeDamage(distance: Double, armorReduction: Double): Double {
    return HEGRENADE_BASE_DAMAGE.times(1.0.minus(distance.div(HEGRENADE_DAMAGE_RANGE)))
        .times(1.0.minus(armorReduction))

}