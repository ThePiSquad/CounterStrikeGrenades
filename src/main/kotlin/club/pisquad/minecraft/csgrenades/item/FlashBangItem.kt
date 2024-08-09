package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.PLAYER_EYESIGHT_OFFSET
import club.pisquad.minecraft.csgrenades.STRONG_THROW_PLAYER_SPEED_SCALE
import club.pisquad.minecraft.csgrenades.WEAK_THROW_PLAYER_SPEED_SCALE
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownType
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.core.Rotations
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus


class FlashBangItem(properties: Properties) : Item(properties) {
    //    private val logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":flashbang_item")
    private var isHoldingBefore: Boolean = false

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {

        // Server side logic
        if (!level.isClientSide) {
            return super.use(level, player, usedHand)
        }

        val speed = player.deltaMovement.scale(WEAK_THROW_PLAYER_SPEED_SCALE)
            .plus(player.lookAngle.normalize().scale(GrenadeThrownType.Weak.speed))
            .length()

        val playerPosition = player.position()

        CsGrenadePacketHandler.INSTANCE.sendToServer(
            GrenadeThrownMessage(
                speed,
                GrenadeType.FLASH_BANG,
                GrenadeThrownType.Weak,
                Vec3(playerPosition.x, playerPosition.y + PLAYER_EYESIGHT_OFFSET, playerPosition.z),
                Rotations(player.xRot, player.yRot, 0.0f),
            )
        )

        return super.use(level, player, usedHand)
    }

    override fun onEntitySwing(stack: ItemStack?, player: LivingEntity?): Boolean {
        if (player == null || !player.level().isClientSide) {
            return false
        }
        val speed =
            player.deltaMovement.scale(STRONG_THROW_PLAYER_SPEED_SCALE)
                .plus(player.lookAngle.normalize().scale(GrenadeThrownType.Strong.speed))
                .length()
        val playerPosition = player.position()

        CsGrenadePacketHandler.INSTANCE.sendToServer(
            GrenadeThrownMessage(
                speed,
                GrenadeType.FLASH_BANG,
                GrenadeThrownType.Strong,
                Vec3(playerPosition.x, playerPosition.y + PLAYER_EYESIGHT_OFFSET, playerPosition.z),
                Rotations(player.xRot, player.yRot, 0.0f),
            )
        )
        return false
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide) return

        if (isSelected) {
            if (!isHoldingBefore) {
                entity.playSound(ModSoundEvents.FLASHBANG_DRAW.get(), 1.0f, 1.0f)
                isHoldingBefore = true
            }
        } else {
            isHoldingBefore = false
        }
    }
}
