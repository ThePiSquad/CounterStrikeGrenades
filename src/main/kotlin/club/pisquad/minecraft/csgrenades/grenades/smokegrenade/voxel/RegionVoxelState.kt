package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeShapeHelper
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class RegionVoxelState(
    @Serializable(with = Vec3Serializer::class) val center: Vec3,
    private val voxels: MutableMap<VoxelPos, ComputeVoxel>
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
}