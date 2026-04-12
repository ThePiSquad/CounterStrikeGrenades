package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.debug

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.GrenadeEntityTracker
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import club.pisquad.minecraft.csgrenades.toVec3
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.joml.Vector3f

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object SmokeGrenadeDebugRenderer {

    private fun smokeGrenadeRenderTargets(): Set<SmokeGrenadeEntity> {
        val player = Minecraft.getInstance().player!!

        @Suppress("UNCHECKED_CAST")
        return GrenadeEntityTracker.get(
            player.level().dimension(),
            GrenadeType.SMOKE_GRENADE
        ) as Set<SmokeGrenadeEntity>
    }

    @JvmStatic
    @SubscribeEvent
    fun renderVoxelOutline(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return
        }
//        if (!SmokeGrenadeDebugOptions.Outline.showOutline) {
//            return
//        }

        val poseStack = event.poseStack
        val camera = event.camera

        poseStack.pushPose()
        poseStack.translate(-camera.position.x, -camera.position.y, -camera.position.z)

        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val smokes = smokeGrenadeRenderTargets()
        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()

        val renderTargets: Set<VoxelPos> = buildSet {
            smokes.forEach { smoke ->
                val voxelMap = smoke.getVoxels() ?: return@forEach
                if (!voxelMap.hasDebug.value) {
                    return@forEach
                }
                if (SmokeGrenadeDebugOptions.Outline.showAll) {
                    addAll(voxelMap.keys)
                } else {
                    addAll(voxelMap.edges.value)
                    if (SmokeGrenadeDebugOptions.Outline.showSpecial) {
                        addAll(voxelMap.filter { (_, voxel) -> voxel.debug?.special ?: false }.keys)
                    }
                }
            }
        }
        renderTargets.forEach { pos ->
            val position = pos.toWorldPos()
            val buffer = bufferSource.getBuffer(RenderType.lines())
            LevelRenderer.renderLineBox(
                poseStack,
                buffer,
                position.x,
                position.y,
                position.z,
                position.x + 0.5,
                position.y + 0.5,
                position.z + 0.5,
                1f,
                1f,
                1f,
                1f
            )
            bufferSource.endLastBatch()
        }


        poseStack.popPose()

        RenderSystem.disableBlend()
        RenderSystem.enableDepthTest()
    }

    @JvmStatic
    @SubscribeEvent
    fun renderVoxelParent(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return
        }
        if (!SmokeGrenadeDebugOptions.Parent.showParent) {
            return
        }
        val camera = event.camera
        val poseStack = event.poseStack
        poseStack.pushPose()
        poseStack.translate(-camera.position.x, -camera.position.y, -camera.position.z)


        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()

        val buffer = bufferSource.getBuffer(RenderType.lines())

        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val buildLine = { voxelPos: VoxelPos, direction: Direction ->
            val start = voxelPos.center
            val end = start.add(direction.normal.toVec3().scale(0.5))
            Pair(start.toVector3f(), end.toVector3f())
        }

        val lines: Set<Pair<Vector3f, Vector3f>> = buildSet {

            val smokes = smokeGrenadeRenderTargets()
            smokes.forEach { smoke ->
                val voxelMap = smoke.getVoxels() ?: return@buildSet
                if (!voxelMap.hasDebug.value) {
                    return@forEach
                }

                if (SmokeGrenadeDebugOptions.Parent.showAll) {
                    voxelMap.forEach { (pos, voxel) ->
                        val direction = voxel.debug!!.parent ?: return@forEach
                        add(buildLine(pos, direction))
                    }
                } else {
                    if (SmokeGrenadeDebugOptions.Parent.showEdges) {
                        voxelMap.edges.value.forEach {
                            val voxel = voxelMap[it]!!
                            val direction = voxel.debug!!.parent ?: return@forEach
                            add(buildLine(it, direction))
                        }
                    }
                    if (SmokeGrenadeDebugOptions.Parent.showSpecial) {
                        voxelMap.specials.value.forEach {
                            val voxel = voxelMap[it]!!
                            val direction = voxel.debug!!.parent ?: return@forEach
                            add(buildLine(it, direction))
                        }
                    }
                }
            }

        }

//        val lines: Set<Pair<Vector3f, Vector3f>> = buildSet {
//            val zero = Vector3f(0f, 0f, 0f)
//            add(Pair(zero, Vector3f(10f, 0f, 0f)))
//            add(Pair(zero, Vector3f(0f, 10f, 0f)))
//            add(Pair(zero, Vector3f(0f, 0f, 10f)))
//        }

        val matrix = poseStack.last().pose()
        val normalMatrix = poseStack.last().normal()

        lines.forEach {
            buffer
                .vertex(matrix, it.first.x, it.first.y, it.first.z)
                .color(0f, 1f, 0f, 1f)
                .normal(normalMatrix, 1f, 1f, 1f)
                .endVertex()
            buffer
                .vertex(matrix, it.second.x, it.second.y, it.second.z)
                .color(0f, 1f, 0f, 1f)
                .normal(normalMatrix, 1f, 1f, 1f)
                .endVertex()
        }
        bufferSource.endLastBatch()

        poseStack.popPose()
        RenderSystem.disableBlend()
        RenderSystem.enableDepthTest()

    }
}