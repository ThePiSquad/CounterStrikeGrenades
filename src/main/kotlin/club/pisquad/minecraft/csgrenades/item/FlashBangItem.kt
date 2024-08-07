package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownType
import net.minecraft.core.Rotations
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class FlashBangItem(properties: Properties) : Item(properties) {
    private val logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":flashbang_item")

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {

        // Server side logic
        if (!level.isClientSide) {
            return super.use(level, player, usedHand)
        }

        // Client side Logic
        CsGrenadePacketHandler.INSTANCE.sendToServer(
            GrenadeThrownMessage(
                player.position(),
                Rotations(player.xRot, player.yRot, 0.0f),
                GrenadeThrownType.Weak,
                GrenadeType.FLASH_BANG
            )
        )

        return super.use(level, player, usedHand)
    }

    override fun onEntitySwing(stack: ItemStack?, player: LivingEntity?): Boolean {
        if (player == null || !player.level().isClientSide) {
            return false
        }

        CsGrenadePacketHandler.INSTANCE.sendToServer(
            GrenadeThrownMessage(
                player.position(),
                Rotations(player.xRot, player.yRot, 0.0f),
                GrenadeThrownType.Strong,
                GrenadeType.FLASH_BANG
            )
        )

        return true
    }

}