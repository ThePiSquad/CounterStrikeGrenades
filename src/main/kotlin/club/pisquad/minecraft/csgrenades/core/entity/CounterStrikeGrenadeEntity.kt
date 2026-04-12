package club.pisquad.minecraft.csgrenades.core.entity

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.ModSettings
import club.pisquad.minecraft.csgrenades.WithGrenadeType
import club.pisquad.minecraft.csgrenades.api.CSGrenadeServerAPI
import club.pisquad.minecraft.csgrenades.api.CSGrenadesAPI
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.trajectory.CustomTrajectoryEntity
import club.pisquad.minecraft.csgrenades.core.entity.trajectory.SubtickNode
import club.pisquad.minecraft.csgrenades.event.GrenadeActivateEvent
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.network.NetworkHooks
import java.util.*
import kotlin.math.pow

interface GrenadeEntityData {
    val sounds: GrenadeSoundEvents
    val damageTypes: GrenadeEntityDamageTypes
}

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out CounterStrikeGrenadeEntity>,
    pLevel: Level,
) :
    CustomTrajectoryEntity(pEntityType, pLevel), GrenadeEntityData, WithGrenadeType {
    lateinit var ownerUuid: UUID
    val rotation: GrenadeRotation

    val owner: Player?
        get() {
            return level().players().find { it.uuid == this.ownerUuid }
        }


    init {
        isNoGravity = true
        noPhysics = true
        rotation = GrenadeRotation(this.id.toLong())
    }

    companion object {
        val isActivatedAccessor: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN)
        val isLandedAccessor: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            ActivateAfterLandingGrenadeEntity::class.java,
            EntityDataSerializers.BOOLEAN
        )
    }

    @Serializable
    private data class SpawnData(
        @Serializable(with = UUIDSerializer::class) val ownerUuid: UUID,
    )

    override fun defineSynchedData() {
        this.entityData.define(isActivatedAccessor, false)
        this.entityData.define(isLandedAccessor, false)
    }

    fun initialize(ownerUuid: UUID, position: Vec3, velocity: Vec3) {
        initializeMovementState(position, velocity)
        this.ownerUuid = ownerUuid
    }


    fun isActivated(): Boolean = this.entityData.get(isActivatedAccessor)

    override fun tick() {
        super.tick()

        if (this.level().isClientSide) {
            this.rotation.tick()
        }
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        CSGrenadeServerAPI.entity.register(this)
    }

    override fun onRemovedFromWorld() {
        CSGrenadeServerAPI.entity.unregister(this.uuid)
        super.onRemovedFromWorld()
    }

    override fun isOnFire(): Boolean = false

    override fun shouldBeSaved(): Boolean = false

    open fun activate() {
        this.entityData.set(isActivatedAccessor, true)
        ModLogger.debug(this) { "Firing GrenadeActivateEvent" }
        MinecraftForge.EVENT_BUS.post(GrenadeActivateEvent(this, this.grenadeType))
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        super.readSpawnData(additionalData)
        val spawnData = ProtoBuf.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        ownerUuid = spawnData.ownerUuid
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        super.writeSpawnData(buffer)
        val spawnData = SpawnData(ownerUuid)
        buffer.writeByteArray(ProtoBuf.encodeToByteArray(SpawnData.serializer(), spawnData))
    }

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {

    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {

    }

    override fun shouldRenderAtSqrDistance(distance: Double): Boolean {
        return distance < ModSettings.SERVER_MESSAGE_RANGE.pow(2)
    }

    override fun onHitBlock(data: SubtickNode.BlockBounceData) {
        ModLogger.info("{} hit block({}) at tick{}", this.grenadeType, data.blockPos, this.tickCount)
        rotation.randomize()
        if (this.level().isClientSide) {
            // EMPTY
        } else {
            CSGrenadesAPI.server.sound.playHitBlockSound(
                this.grenadeType,
                this.uuid,
                this.level() as ServerLevel,
                data.position
            )
        }
    }

    override fun onHitEntity(data: SubtickNode.EntityBounceData) {
        ModLogger.info("{} hit entity({}) at tick{}", this.grenadeType, data.id, this.tickCount)
        rotation.randomize()
        if (this.level().isClientSide) {
            // EMPTY
        } else {
//            playServerEntityHitSound(data.position)
        }
    }

    override fun onTrajectoryComplete() {
        // In current implementation, when a trajectory is completed, that means we have landed on the ground
        onLanding()
    }

    open fun onLanding() {
        this.entityData.set(isLandedAccessor, true)
    }

    override fun hashCode(): Int {
        return this.uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CounterStrikeGrenadeEntity

        return this.uuid == other.uuid
    }

}
