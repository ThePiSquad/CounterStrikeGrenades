package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.SoundTypes
import club.pisquad.minecraft.csgrenades.SoundUtils
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out ThrowableItemProjectile>,
    pLevel: Level,
    val grenadeType: GrenadeType
) :
    ThrowableItemProjectile(pEntityType, pLevel) {

    val speed: Float = 0f
    var isLanded: Boolean = false
    var isExploded: Boolean = false

    var hitBlockSound = ModSoundEvents.GRENADE_HIT.get()

    companion object {
        val speedAccessor: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.FLOAT)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(speedAccessor, speed)
    }


    override fun onHitEntity(result: EntityHitResult) {
        if (this.owner == null) return
        val player = this.owner as Player
        result.entity.hurt(player.damageSources().generic(), 1f)
    }

    override fun tick() {
        super.tick()
        if (this.isLanded || this.isExploded) {
            this.deltaMovement = Vec3.ZERO
            this.isNoGravity = true
        }
    }

    /**
     * Called when the entity is added to the world.
     *
     * This function plays the sound effect of a grenade being thrown when the entity is added to the world.
     *
     * @return None
     */
    override fun onAddedToWorld() {
        this.playSound(ModSoundEvents.GRENADE_THROW.get(), 0.2f, 1f)
    }

    /**
     * Handles the bounce logic for custom grenades when they hit a block.
     *
     * This function updates the entity's movement and position based on the block hit result,
     * simulating a bouncing effect. It also plays a sound effect when the entity hits a block on the client side.
     *
     * @param result The block hit result.
     */
    override fun onHitBlock(result: BlockHitResult) {
//        logger.info("Grenade[@$this] hit block at ${result.blockPos}")

        this.setPos(this.xOld, this.yOld, this.zOld)

        // entity.playSound seems to have a relatively small hearable ange
        // This function seems to be work fine when calling from server and client side?
        // So I just make a test here
        // (In integrated server, haven't tested on other configurations yet)
        if (this.level() is ClientLevel && !isExploded && !isLanded) {
            val player = Minecraft.getInstance().player!!
            val distance = this.position().add(player.position().reverse()).length()
            val soundInstance = EntityBoundSoundInstance(
                hitBlockSound,
                SoundSource.AMBIENT,
                SoundUtils.getVolumeFromDistance(
                    distance,
                    SoundTypes.GRENADE_HIT // unify volume for all grenades hit sounds
                ).toFloat(),
                1f,
                this,
                0
            )
            Minecraft.getInstance().soundManager.play(soundInstance)
        }

        // Calculate the movement of the entity
        this.setPos(this.xOld, this.yOld, this.zOld)
        if (isLanded || isExploded) {
            return

        } else {
            this.deltaMovement = when (result.direction) {
                Direction.UP, Direction.DOWN -> Vec3(deltaMovement.x, -deltaMovement.y, deltaMovement.z)

                Direction.WEST, Direction.EAST ->
                    Vec3(-deltaMovement.x, deltaMovement.y, deltaMovement.z)

                Direction.NORTH, Direction.SOUTH ->
                    Vec3(deltaMovement.x, deltaMovement.y, -deltaMovement.z)

                null -> deltaMovement

            }
//                if (result.isInside) {
            this.setPos(this.xOld, this.yOld, this.zOld)
//                }

            this.deltaMovement = this.deltaMovement.scale(0.5)
        }
        // fix: the entity will keep bouncing on the ground
        if (result.direction == Direction.UP && this.deltaMovement.length() < 0.05) {
            this.setPos(this.x, result.blockPos.y.toDouble() + 1, this.z)
            this.deltaMovement = Vec3.ZERO
            this.isLanded = true
        }
    }

    override fun shouldBeSaved(): Boolean {
        return false
    }

}