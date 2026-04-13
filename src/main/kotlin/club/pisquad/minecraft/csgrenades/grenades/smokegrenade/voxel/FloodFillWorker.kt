package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeConfig
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeShapeHelper
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

class FloodFillWorker(
    val center: Vec3,
    val voxels: RegionVoxelState
) {
    var cycleStart: Set<VoxelPos> = setOf()

    fun compute(): RegionVoxelState {
        val centerPosition = VoxelPos.containing(center)
        voxels[centerPosition]!!.intensity = SmokeGrenadeConfig.spread.initialIntensity.get()

        cycleStart = setOf(centerPosition)

        while (cycleStart.isNotEmpty()) {
            cycleStart = computeCurrentCycle()
        }

        // Fill space below
        // TODO

        return voxels.filterNonEmpty()
    }

    private fun computeCurrentCycle(): Set<VoxelPos> {
        val nextCycle: MutableSet<VoxelPos> = mutableSetOf()

        for (ele in cycleStart) {
            voxels[ele]?.run {
                Direction.entries
                    .filter { this.connectivity.contains(it) }
                    .forEach {
                        val intensity = this.neighborIntensity(it)
                        val target = ele.relative(it)

                        if (!SmokeShapeHelper.isInsideBaseShape(center, target.center)) {
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