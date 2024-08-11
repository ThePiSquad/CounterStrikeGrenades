package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.FlashBangExplodedMessage
import club.pisquad.minecraft.csgrenades.registery.ModItems
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.network.PacketDistributor

class FlashBangEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.FLASH_BANG) {
//    private val logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":flashbang_entity")

    override fun getDefaultItem(): Item {
        return ModItems.FLASH_BANG_ITEM.get()
    }

    override fun tick() {
        super.tick()
//
//        if (this.isExploded) {
//
//        }

        // FlashBang exploded after 1.6 second in CSGO, this logic only for server side
        if (!this.level().isClientSide) {
            if (this.tickCount > 1.6 * 20) {
                CsGrenadePacketHandler.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    FlashBangExplodedMessage(this.id, this.position())
                )
                isExploded = true
                this.kill()
            }
        }

    }
}