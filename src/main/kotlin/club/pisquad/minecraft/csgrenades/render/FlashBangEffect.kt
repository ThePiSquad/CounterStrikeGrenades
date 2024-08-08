package club.pisquad.minecraft.csgrenades.render

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.FLASHBANG_EFFECT_KEY
import club.pisquad.minecraft.csgrenades.FLASHBANG_EFFECT_TAG_DECAY_RATE
import net.minecraft.client.Minecraft
import net.minecraft.util.FastColor
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.max


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashBangEffect {

    @SubscribeEvent
    fun renderOverlay(event: RenderGuiOverlayEvent.Pre) {
        val player = Minecraft.getInstance().player ?: return
        val tag = player.persistentData
        val graphics = event.guiGraphics

        if (tag.getDouble(FLASHBANG_EFFECT_KEY) == 0.0) {
            graphics.flush()
            return
        }

        val flashbangEffect = tag.getDouble(FLASHBANG_EFFECT_KEY)

        if (flashbangEffect < 0) {
            tag.putDouble(FLASHBANG_EFFECT_KEY, 0.0)
            return
        }

        graphics.fill(
            0,
            0,
            graphics.guiWidth(),
            graphics.guiHeight(),
            FastColor.ABGR32.color(
                (flashbangEffect * 255).toInt(),
                255,
                255,
                255
            )
        )
    }

    @SubscribeEvent
    fun tick(event: TickEvent) {
        val player = Minecraft.getInstance().player ?: return
        val tag = player.persistentData
        val flashbangEffect = tag.getDouble(FLASHBANG_EFFECT_KEY)
        if (tag.getDouble(FLASHBANG_EFFECT_KEY) <= 0) {
            return
        }

        tag.putDouble(FLASHBANG_EFFECT_KEY, max(flashbangEffect - FLASHBANG_EFFECT_TAG_DECAY_RATE, 0.0))

    }
}

private fun calculateFlashBangEffect(value: Double) {
    
}