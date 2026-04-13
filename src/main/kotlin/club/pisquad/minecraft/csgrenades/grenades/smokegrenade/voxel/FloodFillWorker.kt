package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeConfig
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeShapeHelper
import club.pisquad.minecraft.csgrenades.horizontalDirections
import club.pisquad.minecraft.csgrenades.isBetween
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.*
import kotlin.random.Random

class FloodFillWorker(
    val center: Vec3,
    val voxels: RegionVoxelState
) {
    var cycleStart: Set<VoxelPos> = setOf()


    fun compute(): RegionVoxelState {
        val centerPosition = VoxelPos.containing(center)
        voxels[centerPosition]!!.intensity = initialIntensity()

        cycleStart = setOf(centerPosition)

        //region Basic shape
        while (cycleStart.isNotEmpty()) {
            cycleStart = spreadOnce(cycleStart, voxels) { voxelPos ->
                SmokeShapeHelper.isInsideBaseShape(center, voxelPos.center)
            }
        }
        //endregion

        //region Squeeze
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
        //endregion

        //region Fill space below
        val bottomLayer = voxels.getBottomLayer().filter { pos ->
            voxels[pos]!!.intensity > 1
        }.toSet()
        val horizontalPossibility = Mth.clamp(0.1 * sqrt(bottomLayer.size.toDouble()), 0.0, 1.0)
        // check for ground below
        val maxFall = SmokeGrenadeConfig.spread.maxFall.get()
        val maxFallVoxelDistance = ceil(maxFall.times(2)).toInt()

        val hasGroundBelow = bottomLayer.any {
            it.run {
                repeat(maxFallVoxelDistance) { distance ->
                    val pos = VoxelPos(this.x, this.y - distance - 1, this.z)
                    val voxel = voxels[pos]
                    if (voxel != null) {
                        if (voxel.connectivity.isBlocking(Direction.UP)) {
                            return@run true
                        }
                    }
                }
                false
            }
        }
        if (hasGroundBelow) {
            cycleStart = bottomLayer
            var counter = 0

            while (!cycleStart.isEmpty() && counter < maxFallVoxelDistance) {
                counter++
                cycleStart = spreadDownwardOnce(cycleStart, voxels, horizontalPossibility)
            }
        }


        //endregion

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

        private fun spreadDownwardOnce(
            elements: Set<VoxelPos>,
            voxels: RegionVoxelState,
            horizontalPossibility: Double,
        ): Set<VoxelPos> {
            check(horizontalPossibility.isBetween(0.0, 1.0))
            val updateQueue: MutableSet<VoxelPos> = mutableSetOf()
            elements.forEach { pos ->
                val voxel = voxels[pos] ?: return@forEach
                val target = voxels[pos.relative(Direction.DOWN)] ?: return@forEach
                val intensity = voxel.neighborIntensity(Direction.DOWN)
                val updateNextLayer = target.triggerIntensityUpdate(Direction.UP, intensity)
                if (updateNextLayer) {
                    updateQueue.add(target.position)
                    val horizontal = Random.nextDouble() < horizontalPossibility
                    if (horizontal) {
                        horizontalDirections().forEach { direction ->
                            val voxel = voxels[pos.relative(Direction.DOWN)]!!
                            val target = voxels[voxel.position.relative(direction)] ?: return@forEach
                            val intensity = voxel.neighborIntensity(direction)
                            val needUpdate = target.triggerIntensityUpdate(direction.opposite, intensity)
                            if (needUpdate) {
                                updateQueue.add(target.position)
                            }
                        }
                    }
                }
            }
            return updateQueue
        }
    }
}