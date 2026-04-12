package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.data

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import kotlinx.serialization.Serializable

@Serializable
sealed interface AttachedSmokeData {
    @Serializable
    class EmptySmokeData : AttachedSmokeData

    @Serializable
    data class SmokeData(
        val activationTime: Long,
        val voxels: VoxelMap
    ) : AttachedSmokeData
}