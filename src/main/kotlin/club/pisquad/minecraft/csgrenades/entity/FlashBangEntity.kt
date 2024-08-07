package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.FlashBangExplodedMessage
import club.pisquad.minecraft.csgrenades.registery.ModItems
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.div
import kotlin.math.sqrt

class FlashBangEntity(pEntityType: EntityType<FlashBangEntity>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel) {
    //    private val logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":flashbang_entity")
    private var speed: Float = 0.0f

    companion object {
        val speedAccessor: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(FlashBangEntity::class.java, EntityDataSerializers.FLOAT)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(speedAccessor, speed)

    }

    override fun getDefaultItem(): Item {
        return ModItems.FLASH_BANG_ITEM.get()
    }

    override fun tick() {
        super.tick()

        // Calculate the speed for entity
        val dx: Double = this.x - this.xo
        val dy: Double = this.y - this.yo
        val dz: Double = this.z - this.zo
        val newSpeed = sqrt(dx * dx + dy * dy + dz * dz) * 20

        this.entityData.set(speedAccessor, newSpeed.toFloat())
//        logger.info("Updating speed, new speed $newSpeed")


        // FlashBang exploded after 1.6 second in CSGO, this logic only for server side
        if (!this.level().isClientSide) {
            if (this.tickCount > 1.6 * 20) {
                CsGrenadePacketHandler.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    FlashBangExplodedMessage(this.position())
                )
                this.kill()

            }
        }

    }

    override fun onHitBlock(result: BlockHitResult) {
//        logger.info("Grenade[@$this] hit block at ${result.blockPos}")

        when (result.direction) {
            Direction.UP, Direction.DOWN -> this.deltaMovement =
                Vec3(deltaMovement.x, -deltaMovement.y, deltaMovement.z)

            Direction.WEST, Direction.EAST -> this.deltaMovement =
                Vec3(-deltaMovement.x, deltaMovement.y, deltaMovement.z)

            Direction.NORTH, Direction.SOUTH -> this.deltaMovement =
                Vec3(deltaMovement.x, deltaMovement.y, -deltaMovement.z)

            null -> {}
        }
        this.setPos(this.xo, this.yo, this.zo)
        this.deltaMovement = this.deltaMovement.div(3.0)
    }

    override fun shouldBeSaved(): Boolean {
        return false
    }

}