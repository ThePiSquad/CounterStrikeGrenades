package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeEntity
import kotlinx.coroutines.*
import kotlin.time.measureTimedValue

class VoxelWorker(entity: SmokeGrenadeEntity) {
    val coroutineWorker: Deferred<RegionVoxelState>

    val floodFillWorker: FloodFillWorker

    init {
        val center = entity.center
        val (state, duration) = measureTimedValue {
            RegionVoxelState.fromCenter(entity.level(), center)
        }
        ModLogger.debug(duration, "Generate region state")

        floodFillWorker = FloodFillWorker(center, state)

        coroutineWorker = ComputeScope.async {
            val (result, duration) = measureTimedValue {
                floodFillWorker.compute()
            }
            ModLogger.debug(duration, "Compute smoke spread")
            result
        }
    }

    companion object {
        val ComputeScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    fun blockingUntilComplete(): VoxelMap {
        val result = runBlocking { coroutineWorker.await() }
        return VoxelMap(result.mapValues { (_, value) -> value.toVoxel() })
    }
}
