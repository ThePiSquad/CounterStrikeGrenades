package club.pisquad.minecraft.csgrenades.client.render.flashbang

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangBlindEffectData
import club.pisquad.minecraft.csgrenades.nanoSecondToSecond
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.util.Mth
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.max

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object BlindEffectRenderManager {
    @Volatile
    var fullBlindDuration: Double = 0.0

    @Volatile
    var totalDuration: Double = 0.0

    @Volatile
    var renderStartTime: Long = System.nanoTime()

    @JvmStatic
    @SubscribeEvent
    fun onRender(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_LEVEL) return

        val timeNow = System.nanoTime()
        val timeDelta = (timeNow - renderStartTime).nanoSecondToSecond()
        if (timeDelta > totalDuration) {
            return
        }

        val player = Minecraft.getInstance().player ?: return

        if (player.isSpectator) {
            return
        }

        renderOverlayWithOpacity(getOpacity(timeDelta), event.poseStack)

    }

    fun schedule(data: FlashbangBlindEffectData) {
        ModLogger.debug("Scheduling new flashbang blind effect render")
        val timeNow = System.nanoTime()
        val timeDelta = (timeNow - renderStartTime).nanoSecondToSecond()
        if (timeDelta > totalDuration) {
            fullBlindDuration = data.fullBlindDuration
            totalDuration = data.totalDuration
            renderStartTime = timeNow
        } else if (timeDelta > fullBlindDuration) {
            totalDuration = max(data.totalDuration, totalDuration - timeDelta)
            fullBlindDuration = data.fullBlindDuration
            renderStartTime = timeNow
        } else {
            totalDuration = max(data.totalDuration, totalDuration - timeDelta)
            fullBlindDuration = max(data.fullBlindDuration, fullBlindDuration - timeDelta)
            renderStartTime = timeNow
        }
        ModLogger.debug(
            "New blind effect render data: fullBlindDuration({}), totalDuration({})",
            fullBlindDuration,
            totalDuration
        )
    }

    private fun getOpacity(timeDelta: Double): Int {
        return if (timeDelta > fullBlindDuration) {
            Mth.lerp((timeDelta - fullBlindDuration).div(totalDuration - fullBlindDuration), 255.0, .0).toInt()
        } else {
            255
        }
    }

    private fun renderOverlayWithOpacity(opacity: Int, poseStack: PoseStack) {
        poseStack.pushPose()
        poseStack.setIdentity()

        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }

        0xFFFFFFFF.toInt()

        val matrix = poseStack.last().pose()

        val tesselator = Tesselator.getInstance()
        val buffer = tesselator.builder
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

        buffer.vertex(matrix, -1.0f, -1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()
        buffer.vertex(matrix, 1.0f, -1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()
        buffer.vertex(matrix, 1.0f, 1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()
        buffer.vertex(matrix, -1.0f, 1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()

        BufferUploader.drawWithShader(buffer.end())

        RenderSystem.disableBlend()
        RenderSystem.enableDepthTest()

        poseStack.popPose()
    }
}
