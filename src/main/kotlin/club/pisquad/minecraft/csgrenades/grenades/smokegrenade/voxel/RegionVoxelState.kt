package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeShapeHelper
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class RegionVoxelState(
    @Serializable(with = Vec3Serializer::class) val center: Vec3,
    val voxels: MutableMap<VoxelPos, ComputeVoxel>
) : MutableMap<VoxelPos, ComputeVoxel> by voxels {

    companion object {
        fun fromCenter(level: Level, center: Vec3): RegionVoxelState {
            val voxels = mutableMapOf<VoxelPos, ComputeVoxel>()

            val blocks = SmokeShapeHelper.getAllPossibleBlocks(center)
            blocks.forEach {
                val context = VoxelBlockContext(it, level.getBlockState(it), level)
                val blockVoxels = VoxelBlockDelegator.delegate(context).voxels(context)
                blockVoxels.forEach { (_, voxel) ->
                    voxels[voxel.position] = voxel
                }
            }

            return RegionVoxelState(center, voxels)
        }
    }

    fun filterNonEmpty(): RegionVoxelState {
        return RegionVoxelState(
            this.center,
            this.filter { (_, state) -> state.intensity > 0 }.toMutableMap()
        )
    }

    fun getVoxelMap(): VoxelMap {
        val voxels = this.filterNonEmpty()
        return VoxelMap(buildMap {
            voxels.forEach { (pos, voxel) ->
                put(pos, voxel.toVoxel())
            }
        })
    }

    fun getBottomLayer(): Set<VoxelPos> {
        val bottomLayer: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()
        this.forEach { (pos, voxel) ->
            if (voxel.intensity > 0) {
                val prevY = bottomLayer.getOrPut(Pair(pos.x, pos.z)) { pos.y }
                if (prevY > pos.y) {
                    bottomLayer[Pair(pos.x, pos.z)] = pos.y
                }
            }
        }
        return buildSet {
            bottomLayer.forEach { (xz, y) ->
                add(VoxelPos(xz.first, y, xz.second))
            }
        }
    }
}