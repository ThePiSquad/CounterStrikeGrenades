package club.pisquad.minecraft.csgrenades.render

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import club.pisquad.minecraft.csgrenades.toVec3i
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.util.FastColor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.max
import kotlin.math.min

data class FlashBangEffectData(
    val fullyBlindedTime: Double,
    val totalEffectTime: Double,
    val flashbangPos: Vec3,
    val ringVolume: Float,
    val blockingFactor: Double,
) {
    val fadeOutTime: Double = totalEffectTime - fullyBlindedTime


    companion object {
        private fun getDistanceFactor(distance: Double): Double {
            return max(-0.015 * distance + 1, 0.0)
        }

        private fun getBlockingFactor(flashbangPos: Vec3, playerEyePos: Vec3): Double {

            var blockingFactor = 1.0
            val playerToFlashBangVec = flashbangPos.add(playerEyePos.reverse())
            val direction = playerToFlashBangVec.normalize()
            val level = Minecraft.getInstance().level ?: return blockingFactor

//
            for (i in 1..playerToFlashBangVec.length().toInt()) {
                val blockState =
                    level.getBlockState(BlockPos(playerEyePos.add(direction.scale(i.toDouble())).toVec3i()))
                blockingFactor -= getBlockingFactorDelta(blockState)
                if (blockingFactor <= 0.0) {
                    return 0.0
                }

            }
            return blockingFactor
        }


        fun create(angle: Double, distance: Double, flashbangPos: Vec3, playerPos: Vec3): FlashBangEffectData {
            val distanceFactor = getDistanceFactor(distance)
            val blockingFactor = getBlockingFactor(flashbangPos, playerPos)

            val fullyBlindedTime = when (angle) {
                in 0.0..53.0 -> 1.88
                in 53.0..72.0 -> 0.45
                in 72.0..101.0 -> 0.08
                in 101.0..180.0 -> 0.08
                else -> 0.0
            } * distanceFactor * blockingFactor

            val totalEffectTime = when (angle) {
                in 0.0..53.0 -> 4.87
                in 53.0..72.0 -> 3.4
                in 72.0..101.0 -> 1.95
                in 101.0..180.0 -> 0.95
                else -> 0.0
            } * distanceFactor * blockingFactor


            return FlashBangEffectData(
                fullyBlindedTime = fullyBlindedTime,
                totalEffectTime = totalEffectTime,
                flashbangPos = flashbangPos,
                ringVolume = 0.8f.times(distanceFactor.toFloat()),
                blockingFactor = blockingFactor
            )
        }
    }
}


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashBangEffect {
    private var tickCount = 0

    private var rendering: Boolean = false

    private var effectData: FlashBangEffectData? = null

    private var soundInstance: SoundInstance? = null

//    private var oldSourceVolume: Float = 0f

    fun render(effectData: FlashBangEffectData) {
        if (effectData.totalEffectTime <= 0.0) return

        if (rendering && this.effectData!!.totalEffectTime - getTimeFromTickCount(tickCount.toDouble()) < effectData.totalEffectTime) {
            renderFinished()
        }
        renderStart(effectData)

    }

    private fun renderStart(effectData: FlashBangEffectData) {
        rendering = true
        this.effectData = effectData

        soundInstance = EntityBoundSoundInstance(
            ModSoundEvents.FLASHBANG_EXPLOSION_RING.get(),
            SoundSource.AMBIENT,
            effectData.ringVolume,
            1f,
            Minecraft.getInstance().player!!,
            0
        )
        val soundManager = Minecraft.getInstance().soundManager
        soundManager.play(soundInstance!!)

    }

    private fun renderFinished() {
        tickCount = 0
        rendering = false
        this.effectData = null
        Minecraft.getInstance().soundManager.stop(soundInstance!!)
        soundInstance = null
//        Minecraft.getInstance().soundManager.updateSourceVolume(SoundSource.AMBIENT, oldSourceVolume)
    }

    @SubscribeEvent
    fun renderOverlay(event: RenderGuiOverlayEvent.Pre) {
        if (!rendering) return

        val graphics = event.guiGraphics
        val partialTick = event.partialTick

        graphics.fill(
            0,
            0,
            graphics.guiWidth(),
            graphics.guiHeight(),
            FastColor.ABGR32.color(
                getGuiOverlayOpacity(
                    partialTick
                ),
                255,
                255,
                255
            )
        )
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
//        if (event.phase == TickEvent.Phase.END) return
        if (effectData == null || !rendering) return
        tickCount++
        if (getTimeFromTickCount(tickCount + 0.0) > effectData!!.totalEffectTime) {
            renderFinished()
            return
        }
    }


    private fun getGuiOverlayOpacity(partialTick: Float): Int {
        if (!rendering || effectData == null) return 0

        val timeSinceBegin = getTimeFromTickCount(tickCount + partialTick.toDouble())
        if (timeSinceBegin < effectData!!.fullyBlindedTime) {
            return 255
        }
        val opacity = min(255, max(0, (30 * (effectData!!.totalEffectTime - timeSinceBegin)).toInt()))
        return opacity
    }
}

fun getTimeFromTickCount(tickCount: Double): Double {
    return tickCount / 20.0
}

fun getBlockingFactorDelta(blockState: BlockState): Double {
    if (blockState.isAir) {
        return 0.0
    }
    if (blockState.canOcclude()) {
        return 1.0
    }
    return 0.2
}