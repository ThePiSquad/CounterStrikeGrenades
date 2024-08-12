package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.helper.TickHelper
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

private var drawSoundPlayedSlot: Int = -1


open class CounterStrikeGrenadeItem(properties: Properties) : Item(properties) {

    lateinit var grenadeType: GrenadeType

    // Sounds
    var drawSound: SoundEvent = SoundEvents.EMPTY

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide) return
        if (entity !is Player) return

        if (isSelected && drawSoundPlayedSlot != slotId) {
            entity.playSound(drawSound, 0.2f, 1.0f)
            drawSoundPlayedSlot = slotId
        }
        if (entity.inventory.selected != drawSoundPlayedSlot) {
            drawSoundPlayedSlot = -1
        }

    }

    fun throwAction(player: Player, throwType: GrenadeThrowType) {
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
    private const val TICK_HELPER_KEY = "GRENADE_THROW_COOLDOWN"

    init {
        TickHelper.create(TICK_HELPER_KEY)
    }

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!event.level.isClientSide) return
        if (TickHelper.get(TICK_HELPER_KEY) < GRENADE_THROW_COOLDOWN) return

        val itemInHand = event.entity.getItemInHand(event.hand).item
        if (itemInHand !is CounterStrikeGrenadeItem) return


        when (event) {
            is PlayerInteractEvent.LeftClickBlock, is PlayerInteractEvent.LeftClickEmpty -> {
                itemInHand.throwAction(event.entity, GrenadeThrowType.Strong)
            }

            is PlayerInteractEvent.RightClickBlock, is PlayerInteractEvent.RightClickItem -> {
                itemInHand.throwAction(event.entity, GrenadeThrowType.Weak)
            }
        }
        TickHelper.reset(TICK_HELPER_KEY)
    }
}