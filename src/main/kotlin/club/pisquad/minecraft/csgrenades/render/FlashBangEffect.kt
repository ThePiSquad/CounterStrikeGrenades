package club.pisquad.minecraft.csgrenades.render

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.helper.TickHelper
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.util.FastColor
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.max
import kotlin.math.min

data class FlashBangEffectData(
    val flashBangEntity: Entity,
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


        fun create(
            flashBangEntity: Entity,
            angle: Double,
            distance: Double,
            flashbangPos: Vec3,
            playerPos: Vec3
        ): FlashBangEffectData {
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
                flashBangEntity,
                fullyBlindedTime = fullyBlindedTime,
                totalEffectTime = totalEffectTime,
                flashbangPos = flashbangPos,
                ringVolume = SoundUtils.getVolumeFromDistance(distance, SoundTypes.FLASHBANG_RING).toFloat(),
                blockingFactor = blockingFactor
            )
        }
    }
}


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashBangEffect {
    private const val TICK_HELPER_KEY = "FLASHBANG_EFFECT"
    private var rendering: Boolean = false

    private var effectData: FlashBangEffectData? = null

    private var ringSoundInstance: SoundInstance? = null
    private var explosionSoundInstance: SoundInstance? = null

    init {
        TickHelper.create(TICK_HELPER_KEY)
    }


    fun render(effectData: FlashBangEffectData) {
        val soundManager = Minecraft.getInstance().soundManager
        val distance = Minecraft.getInstance().player!!.position().distanceTo(effectData.flashbangPos)

        // The flashbang entity was killed in previous procedure
        explosionSoundInstance = SimpleSoundInstance(
            ModSoundEvents.FLASHBANG_EXPLODE.get(),
            SoundSource.AMBIENT,
            SoundUtils.getVolumeFromDistance(distance, SoundTypes.FLASHBANG_EXPLODE).toFloat(),
            1f,
            RandomSource.create(),
            effectData.flashbangPos.x,
            effectData.flashbangPos.y,
            effectData.flashbangPos.z
        )
        soundManager.play(explosionSoundInstance!!)

        if (effectData.totalEffectTime <= 0.0) return // If the flashbang is blocked

        if (rendering) {
            if (this.effectData!!.totalEffectTime - getTimeFromTickCount(
                    TickHelper.get(TICK_HELPER_KEY).toDouble()
                ) < effectData.totalEffectTime
            ) {
                renderFinished()
                renderStart(effectData)
            }
        } else {
            renderStart(effectData)
        }
    }

    private fun renderStart(effectData: FlashBangEffectData) {
        rendering = true
        this.effectData = effectData
        TickHelper.reset(TICK_HELPER_KEY)

        val soundManager = Minecraft.getInstance().soundManager

        ringSoundInstance = EntityBoundSoundInstance(
            ModSoundEvents.FLASHBANG_EXPLOSION_RING.get(),
            SoundSource.AMBIENT,
            effectData.ringVolume,
            1f,
            Minecraft.getInstance().player!!,
            0
        )
        soundManager.play(ringSoundInstance!!)
    }

    private fun renderFinished() {
        TickHelper.reset(TICK_HELPER_KEY)
        rendering = false
        this.effectData = null
        Minecraft.getInstance().soundManager.stop(ringSoundInstance!!)
        ringSoundInstance = null
    }

    @SubscribeEvent
    fun renderOverlay(event: RenderGuiOverlayEvent.Pre) {
        if (!rendering) return

        if (getTimeFromTickCount(TickHelper.get(TICK_HELPER_KEY).toDouble()) > effectData!!.totalEffectTime) {
            renderFinished()
            return
        }
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

    private fun getGuiOverlayOpacity(partialTick: Float): Int {
        if (!rendering || effectData == null) return 0

        val timeSinceBegin = getTimeFromTickCount(TickHelper.get(TICK_HELPER_KEY) + partialTick.toDouble())
        if (timeSinceBegin < effectData!!.fullyBlindedTime) {
            return 255
        }
        val opacity = min(255, max(0, (30 * (effectData!!.totalEffectTime - timeSinceBegin)).toInt()))
        return opacity
    }
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