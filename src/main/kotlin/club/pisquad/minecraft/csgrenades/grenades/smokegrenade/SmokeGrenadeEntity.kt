package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.data.AttachedSmokeData
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokeGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelWorker
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.runOnServer
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class SmokeGrenadeEntity(pEntityType: EntityType<out SmokeGrenadeEntity>, pLevel: Level) :
    ActivateAfterLandingGrenadeEntity(
        pEntityType,
        pLevel,
        SmokeGrenadeConfig.common.fuseTime.get().toTick().toInt(),
    ) {
    var voxelWorker: VoxelWorker? = null

    override val sounds = SmokeGrenadeRegistries.sounds
    override val damageTypes = SmokeGrenadeRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.SMOKE_GRENADE

    companion object {
        val smokeDataAccessor: EntityDataAccessor<AttachedSmokeData> = SynchedEntityData.defineId(
            SmokeGrenadeEntity::class.java, SmokeGrenadeRegistries.serializers.smokeData
        )
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(smokeDataAccessor, AttachedSmokeData.EmptySmokeData())
    }


    override fun onLanding() {
        super.onLanding()

        this.runOnServer {
            voxelWorker = VoxelWorker(this)
        }
    }

    override fun activate() {
        super.activate()
        this.runOnServer {
            val time = System.currentTimeMillis()
            val voxelMap = voxelWorker!!.blockingUntilComplete()

            ModLogger.info(this) { "Voxel calculation done, none empty voxel count:{}".format(voxelMap.size) }

            val data = AttachedSmokeData.SmokeData(time, voxelMap)
            this.entityData.set(smokeDataAccessor, data)

            val message = SmokeGrenadeActivatedMessage(
                this.id
            )

            ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.center, message)
        }
    }

    fun getVoxels(): VoxelMap? {
        val data = this.entityData.get(smokeDataAccessor)
        return if (data is AttachedSmokeData.EmptySmokeData) {
            null
        } else {
            (data as AttachedSmokeData.SmokeData).voxels
        }
    }
}