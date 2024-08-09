package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrowType
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
import net.minecraft.core.Rotations
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


open class CounterStrikeGrenadeItem(properties: Properties) : Item(properties) {
    private var isHoldingBefore: Boolean = false

    // Sounds
    private var drawSound: SoundEvent = SoundEvents.EMPTY

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide) return

        if (isSelected) {
            if (!isHoldingBefore) {
                entity.playSound(drawSound, 1.0f, 1.0f)
                isHoldingBefore = true
            }
        } else {
            isHoldingBefore = false
        }
    }

    fun throwAction(player: Player, grenadeType: GrenadeType, throwType: GrenadeThrowType) {
        val playerSpeedFactor = when (throwType) {
            GrenadeThrowType.Strong -> STRONG_THROW_PLAYER_SPEED_FACTOR
            GrenadeThrowType.Weak -> WEAK_THROW_PLAYER_SPEED_FACTOR
        }

        val speed = player.deltaMovement.scale(playerSpeedFactor)
            .add(player.lookAngle.normalize().scale(throwType.speed))
            .length()
        val playerPos = player.position()
        CsGrenadePacketHandler.INSTANCE.sendToServer(
            GrenadeThrownMessage(
                player.uuid,
                speed,
                grenadeType,
                Vec3(playerPos.x, playerPos.y + PLAYER_EYESIGHT_OFFSET, playerPos.z),
                Rotations(player.xRot, player.yRot, 0.0f),
            )
        )
    }
}

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object PlayerInteractEventHandler {
    private var tickCount: Int = 0

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!event.level.isClientSide) return
        if (tickCount < GRENADE_THROW_COOLDOWN) return

        val itemInHand = event.entity.getItemInHand(event.hand).item
        if (itemInHand !is CounterStrikeGrenadeItem) return

        val grenadeType = when (itemInHand) {
            is FlashBangItem -> GrenadeType.FLASH_BANG
            else -> {
                return
            }
        }

        when (event) {
            is PlayerInteractEvent.LeftClickBlock, is PlayerInteractEvent.LeftClickEmpty -> {
                itemInHand.throwAction(event.entity, grenadeType, GrenadeThrowType.Strong)
            }

            is PlayerInteractEvent.RightClickBlock, is PlayerInteractEvent.RightClickItem -> {
                itemInHand.throwAction(event.entity, grenadeType, GrenadeThrowType.Weak)
            }
        }
        tickCount = 0
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        tickCount++
    }

    fun register(bus: IEventBus) {
        bus.register(::onPlayerInteract)
        bus.register(::tick)

    }
}