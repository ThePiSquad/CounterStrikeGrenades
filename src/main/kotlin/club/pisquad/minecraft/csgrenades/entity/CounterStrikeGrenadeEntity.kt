package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<FlashBangEntity>,
    pLevel: Level,
    val grenadeType: GrenadeType
) :
    ThrowableItemProjectile(pEntityType, pLevel) {

    private val speed: Float = 0f

    companion object {
        val speedAccessor: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(FlashBangEntity::class.java, EntityDataSerializers.FLOAT)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(speedAccessor, speed)
    }


    override fun onHitEntity(result: EntityHitResult) {
    }

    override fun tick() {
        super.tick()

        // Calculate the speed for entity
        val dx: Double = this.x - this.xo
        val dy: Double = this.y - this.yo
        val dz: Double = this.z - this.zo
        val newSpeed = sqrt(dx * dx + dy * dy + dz * dz) * 20

        this.entityData.set(speedAccessor, newSpeed.toFloat())

    }

    override fun onAddedToWorld() {
        this.playSound(ModSoundEvents.GRENADE_THROW.get(), 1f, 1f)
    }

    override fun onHitBlock(result: BlockHitResult) {
//        logger.info("Grenade[@$this] hit block at ${result.blockPos}")
        this.deltaMovement = when (result.direction) {
            Direction.UP, Direction.DOWN -> Vec3(deltaMovement.x, -deltaMovement.y, deltaMovement.z)

            Direction.WEST, Direction.EAST ->
                Vec3(-deltaMovement.x, deltaMovement.y, deltaMovement.z)

            Direction.NORTH, Direction.SOUTH ->
                Vec3(deltaMovement.x, deltaMovement.y, -deltaMovement.z)

            null -> deltaMovement

        }
        if (result.isInside) {
            this.setPos(this.xOld, this.yOld, this.zOld)
        }

        this.deltaMovement = this.deltaMovement.scale(0.5)

        this.playSound(ModSoundEvents.GRENADE_HIT.get(), 1f, 1f)
    }

}