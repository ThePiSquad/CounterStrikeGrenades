package club.pisquad.minecraft.csgrenades.core.entity.trajectory

import club.pisquad.minecraft.csgrenades.ModSettings.Entity.POSITION_ERROR_TOLERANCE
import club.pisquad.minecraft.csgrenades.ModSettings.Entity.Physics.MINIMUM_VELOCITY_AFTER_BOUNCE
import club.pisquad.minecraft.csgrenades.isBetween
import club.pisquad.minecraft.csgrenades.math.Segment
import club.pisquad.minecraft.csgrenades.minus
import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.absoluteValue


interface TrajectoryNode {
    val position: Vec3
    val velocity: Vec3
}

@Serializable
class TickNode(
    val tick: Int,
    @Serializable(with = Vec3Serializer::class) override val position: Vec3,
    @Serializable(with = Vec3Serializer::class) override val velocity: Vec3,
    val completed: Boolean = false,
    val subtickNodes: MutableList<SubtickNode> = mutableListOf(),
) : TrajectoryNode {

    companion object;

    /**Calculates the subtick nodes within this tick
     * @return Next TickNode
     * */
    fun processTick(
        level: Level
    ): TickNode {
        if (this.completed) {
            return this
        }

        var partialTick = 0.0
        var completed = false
        val subtickNodes: MutableList<SubtickNode> = mutableListOf()

        var lastNode: SubtickNode = this.toSubtickNode()


        while (true) {
            lastNode = tryTravelInDirection(
                level, lastNode.position, lastNode.velocity, lastNode.partialTick
            )
            if (lastNode.partialTick > 1.0) {
                break
            }

            if (lastNode.partialTick.minus(partialTick).absoluteValue < 0.001) {
                // Somehow we stuck here?
                // Grenade keep bouncing between two adjacent surfaces
//                return TickNode(this.tick + 1, lastNode.velocity, Vec3.ZERO, true)
                completed = true
                break
            }
            if (lastNode.velocity.length() < MINIMUM_VELOCITY_AFTER_BOUNCE) {
//                return TickNode(this.tick + 1, lastNode.velocity, Vec3.ZERO, true)
                completed = true
                break
            }
            partialTick = lastNode.partialTick
            subtickNodes.add(lastNode)
        }

        return TickNode(this.tick + 1, lastNode.position, lastNode.velocity, completed, subtickNodes)
    }

    fun toSubtickNode(): SubtickNode {
        return SubtickNode(
            this.position,
            this.velocity,
            0.0,
            bounceData = null
        )
    }

    private fun tryTravelInDirection(
        level: Level,
        position: Vec3,
        velocity: Vec3,
        partialTick: Double,
    ): SubtickNode {
        assert((1 - partialTick).isBetween(0.0, 1.0))

        val deltaMovement = velocity.scale(1 - partialTick)
        val blocksInPath = PhysicsHelper.getBlocksInPath(
            Segment(
                position, position.add(deltaMovement),
            ),
        ).filter { BounceHelper.shouldBounceOnBlock(level, it) }

        for (block in blocksInPath) {
            val bounceResult = BounceHelper.tryBounce(level, block, position, deltaMovement, velocity)
            when (bounceResult.type) {
                BounceHelper.BounceResultTypes.THROUGH -> {
                    continue
                }

                BounceHelper.BounceResultTypes.BOUNCE -> {
                    return SubtickNode(
                        bounceResult.bouncePoint,
                        bounceResult.newVelocity,
                        partialTick + bounceResult.tickDelta,
                        SubtickNode.BlockBounceData(
                            block,
                            bounceResult.bouncePoint,
                            bounceResult.newVelocity,
                            bounceResult.direction!!
                        )
                    )
                }

                BounceHelper.BounceResultTypes.HIT_ENTITY -> {
                    throw NotImplementedError()
//                    return SubtickNode(
//                        bounceResult.bouncePoint,
//                        bounceResult.newVelocity,
//                        partialTick + bounceResult.tickDelta,
//                    )
                }
            }
        }
        return SubtickNode(
            position.add(deltaMovement), PhysicsHelper.applyVelocityPhysics(velocity, 1.0), 2.0, null
        )

    }

    // Compare client node with server, returns whether it needs correction
    // Only compare for position error since any velocity error will eventually represent as an position error
    fun compareServerNode(node: TickNode): Boolean {
        return this.position.minus(node.position)
            .length() > POSITION_ERROR_TOLERANCE
    }
}

@Serializable
data class SubtickNode(
    @Serializable(with = Vec3Serializer::class) override val position: Vec3,
    @Serializable(with = Vec3Serializer::class) override val velocity: Vec3,
    val partialTick: Double,
    val bounceData: BounceData?
) : TrajectoryNode {
    @Serializable
    sealed class BounceData

    @Serializable
    class BlockBounceData(
        @Serializable(with = BlockPosSerializer::class) val blockPos: BlockPos,
        @Serializable(with = Vec3Serializer::class) val position: Vec3,
        @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
        val direction: Direction,
    ) : BounceData()

    @Serializable
    class EntityBounceData(
        val id: Int,
        @Serializable(with = Vec3Serializer::class) val position: Vec3,
    ) : BounceData()
}
