package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeConfig
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeShapeHelper
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.*

class FloodFillWorker(
    val center: Vec3,
    val voxels: RegionVoxelState
) {
    var cycleStart: Set<VoxelPos> = setOf()


    fun compute(): RegionVoxelState {
        val centerPosition = VoxelPos.containing(center)
        voxels[centerPosition]!!.intensity = initialIntensity()

        cycleStart = setOf(centerPosition)

        //Basic shape
        while (cycleStart.isNotEmpty()) {
            cycleStart = spreadOnce(cycleStart, voxels) { voxelPos ->
                SmokeShapeHelper.isInsideBaseShape(center, voxelPos.center)
            }
        }

        //Squeeze
        val edges = voxels.getVoxelMap().edges.value.filter { pos ->
            // Test if any neighbor is spreadable
            // Which means this voxel's spread is terminated by the shape checker/ or terminated by not able to spread
            // Which means this voxel is the edge we are looking for
            Direction.entries.any { direction ->
                val target = voxels[pos.relative(direction)] ?: return@filter false
                target.intensity == 0 && target.triggerIntensityUpdate(direction.opposite, Int.MAX_VALUE)
            }
        }

        if (edges.isNotEmpty()) {
            val totalCompensate = max(0, expectedVoxelMapSize() - voxels.filterNonEmpty().size)
            val compensatePerVoxel = 3 * cbrt(totalCompensate.toDouble()).toInt()

            if (compensatePerVoxel > 0) {
                cycleStart = edges.toSet()
                edges.forEach {
                    voxels[it]!!.intensity = compensatePerVoxel
                }
                while (cycleStart.isNotEmpty()) {
                    cycleStart = spreadOnce(cycleStart, voxels) { true }
                }
            }
        }
        // Fill space below
        // TODO

        return voxels.filterNonEmpty()
    }

    companion object {

        private fun expectedVoxelMapSize(): Int {
            val baseSize = SmokeShapeHelper.baseShapeSize()
            val centerLevelSize = SmokeShapeHelper.centerLevelSize()
            return baseSize - (baseSize - centerLevelSize).div(2)
        }

        private fun initialIntensity(): Int {
            val width = ceil(SmokeGrenadeConfig.spread.smokeWidth.get()).toInt()
            val height = ceil(SmokeGrenadeConfig.spread.smokeHeight.get()).toInt()
            return max(
                (width * 2).div(sin(PI.div(4))).toInt(),
                (height * 2).div(sin(PI.div(4))).toInt()
            )
        }


        private fun spreadOnce(
            elements: Set<VoxelPos>,
            voxels: RegionVoxelState,
            positionCheck: (VoxelPos) -> Boolean,
        ): Set<VoxelPos> {
            val nextCycle: MutableSet<VoxelPos> = mutableSetOf()

            for (ele in elements) {
                voxels[ele]?.run {
                    Direction.entries
                        .filter { this.connectivity.contains(it) }
                        .forEach {
                            val intensity = this.neighborIntensity(it)
                            val target = ele.relative(it)

                            if (!positionCheck(target)) {
                                return@forEach
                            }

                            val voxel = voxels[target] ?: return@forEach

                            val needUpdate = voxel.triggerIntensityUpdate(it.opposite, intensity)
                            if (needUpdate) {
                                nextCycle.add(target)
                            }
                        }
                }
            }
            return nextCycle
        }
    }
}