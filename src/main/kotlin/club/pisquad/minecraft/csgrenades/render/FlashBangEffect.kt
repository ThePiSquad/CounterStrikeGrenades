package club.pisquad.minecraft.csgrenades.render

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.FLASHBANG_EFFECT_TAG_DECAY_RATE
import club.pisquad.minecraft.csgrenades.TICK_BEFORE_FLASHBANG_DECAY_BASE_VALUE
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundSource
import net.minecraft.util.FastColor
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.max
import kotlin.math.min


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashBangEffect {
    private var flashBangEffectValue: Double = 0.0
    private var shouldDecay: Boolean = false

    private var tickCount = 0;
    private var tickBeforeDecay = TICK_BEFORE_FLASHBANG_DECAY_BASE_VALUE

    private var rendering: Boolean = false

    private var soundInstance: SoundInstance? = null

    private var oldSourceVolume: Float = 0f

    fun render(value: Double) {
        flashBangEffectValue = max(value, flashBangEffectValue)
        tickCount = 0
        shouldDecay = false
        rendering = true

        tickBeforeDecay = (TICK_BEFORE_FLASHBANG_DECAY_BASE_VALUE * flashBangEffectValue / 2).toInt()
//        Minecraft.getInstance().player?.playSound(ModSoundEvents.FLASHBANG_EXPLOSION_RING.get(), 1f, 1f)
        soundInstance = EntityBoundSoundInstance(
            ModSoundEvents.FLASHBANG_EXPLOSION_RING.get(),
            SoundSource.AMBIENT,
            flashBangEffectValue.div(5).toFloat(),
            1f,
            Minecraft.getInstance().player!!,
            0
        )
        val soundManager = Minecraft.getInstance().soundManager
        soundManager.play(soundInstance!!)
        oldSourceVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.AMBIENT)
    }

    private fun renderFinished() {
        shouldDecay = true
        tickCount = 0
        Minecraft.getInstance().soundManager.stop(soundInstance!!)
        soundInstance = null
        Minecraft.getInstance().soundManager.updateSourceVolume(SoundSource.AMBIENT, oldSourceVolume)
    }

    @SubscribeEvent
    fun renderOverlay(event: RenderGuiOverlayEvent.Pre) {
        if (!rendering) return

        val graphics = event.guiGraphics
        val partialTick = event.partialTick

        if (flashBangEffectValue == 0.0) {
            rendering = false
            renderFinished()
            graphics.flush()
            return
        }
        Minecraft.getInstance().soundManager.updateSourceVolume(SoundSource.AMBIENT, flashBangEffectValue.toFloat())

        graphics.fill(
            0,
            0,
            graphics.guiWidth(),
            graphics.guiHeight(),
            FastColor.ABGR32.color(
                getGuiOverlayOpacity(flashBangEffectValue, partialTick, shouldDecay),
                255,
                255,
                255
            )
        )
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (flashBangEffectValue == 0.0) return
        if (!rendering) return

        tickCount += 1

        if (tickCount > tickBeforeDecay) {
            shouldDecay = true
        }

        if (shouldDecay) {
            flashBangEffectValue = max(flashBangEffectValue - FLASHBANG_EFFECT_TAG_DECAY_RATE, 0.0)
        }
    }
}


private fun getGuiOverlayOpacity(flashbangEffect: Double, partialTick: Float, decay: Boolean): Int {
    val partialTickDelta = if (decay) FLASHBANG_EFFECT_TAG_DECAY_RATE * partialTick else 0
    val a = (flashbangEffect - partialTickDelta.toDouble()) * 40
    return min(40, max(a.toInt(), 0))

}

//private fun getRingVolume(flashbangEffect: Double): Float {
//    return
//
//}