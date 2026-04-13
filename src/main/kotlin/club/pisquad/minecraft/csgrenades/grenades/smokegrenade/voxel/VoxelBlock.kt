package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.math.Quadrant
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.*
import java.util.*

sealed interface VoxelBlock {
    fun check(context: VoxelBlockContext): Boolean
    fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel>
}

object VoxelBlockDelegator {
    val candidates: List<VoxelBlock> = buildList {
        add(SolidVoxelBlock)
        add(SlabVoxelBlock)
        add(StairVoxelBlock)
        add(CrossCollisionVoxelBlock)
        add(DoorVoxelBlock)
        add(TrapdoorVoxelBlock)
        add(SignVoxelBlock)
        add(BedVoxelBlock)
        add(FenceGateVoxelBlock)
        add(ChessVoxelBlock)
        add(AirVoxelBlock)
    }

    fun delegate(context: VoxelBlockContext): VoxelBlock {
        return candidates.find {
            it.check(context)
        } ?: SolidVoxelBlock
    }
}

data class VoxelBlockContext(
    val position: BlockPos,
    val blockState: BlockState,
    val level: Level
)

object AirVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.isAir
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.position, it,
                        ComputeVoxel.Connectivity.ALL,
                        false
                    )
                )
            }
        }
    }
}

object SolidVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.isCollisionShapeFullBlock(context.level, context.position)
                || !context.blockState.fluidState.isEmpty // Also check for waterlogged blocks
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.position, it,
                        ComputeVoxel.Connectivity.NONE,
                    )
                )
            }
        }
    }
}

object DoorVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.DOORS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val opened = context.blockState.getValue(BlockStateProperties.OPEN)
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val hinge = context.blockState.getValue(BlockStateProperties.DOOR_HINGE)

        val blockingSide = if (opened) {
            if (hinge == DoorHingeSide.LEFT) {
                facing.opposite.clockWise
            } else {
                facing.opposite.counterClockWise
            }
        } else {
            facing.opposite
        }

        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (it.x == blockingSide || it.z == blockingSide) {
                    ComputeVoxel.Connectivity.exclude(blockingSide)
                } else {
                    ComputeVoxel.Connectivity.ALL
                }
                put(
                    it, ComputeVoxel.create(
                        context.position, it,
                        connectivity,
                    )
                )
            }
        }
    }
}

object TrapdoorVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.TRAPDOORS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val opened = context.blockState.getValue(BlockStateProperties.OPEN)
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val half = context.blockState.getValue(BlockStateProperties.HALF)

        val blockingSide = if (opened) {
            facing.opposite
        } else {
            if (half == Half.TOP) {
                Direction.UP
            } else {
                Direction.DOWN
            }
        }
        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (it.x == blockingSide || it.y == blockingSide || it.z == blockingSide) {
                    ComputeVoxel.Connectivity.exclude(blockingSide)
                } else {
                    ComputeVoxel.Connectivity.ALL
                }
                put(
                    it, ComputeVoxel.create(
                        context.position, it,
                        connectivity,
                    )
                )
            }
        }
    }
}

object SignVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.ALL_HANGING_SIGNS) || context.blockState.`is`(BlockTags.SIGNS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.position, it,
                        ComputeVoxel.Connectivity.ALL
                    )
                )
            }
        }
    }
}

object SlabVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.SLABS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val type = context.blockState.getValue(BlockStateProperties.SLAB_TYPE)
        val directions = when (type) {
            SlabType.TOP -> listOf(Direction.UP)
            SlabType.BOTTOM -> listOf(Direction.DOWN)
            SlabType.DOUBLE -> listOf(Direction.UP, Direction.DOWN)
        }
        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (directions.contains(it.y)) {
                    ComputeVoxel.Connectivity.NONE
                } else {
                    ComputeVoxel.Connectivity.ALL
                }
                put(
                    it, ComputeVoxel(
                        VoxelPos.fromBlockAndQuadrant(context.position, it),
                        connectivity,
                    )
                )
            }
        }
    }
}

object CrossCollisionVoxelBlock : VoxelBlock {

    // Iron bars
    // Fences
    // Glass panes
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is CrossCollisionBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val north = context.blockState.getValue(BlockStateProperties.NORTH)
        val south = context.blockState.getValue(BlockStateProperties.SOUTH)
        val west = context.blockState.getValue(BlockStateProperties.WEST)
        val east = context.blockState.getValue(BlockStateProperties.EAST)

        val quadrantConnectivity: MutableMap<Quadrant, EnumSet<Direction>> = buildMap {
            Quadrant.entries.forEach {
                put(it, EnumSet.allOf(Direction::class.java))
            }
        }.toMutableMap()

        if (north) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.z == Direction.NORTH) {
                    directions.remove(quadrant.x.opposite)
                }
            }
        }
        if (south) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.z == Direction.SOUTH) {
                    directions.remove(quadrant.x.opposite)
                }
            }
        }
        if (west) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.x == Direction.WEST) {
                    directions.remove(quadrant.z.opposite)
                }
            }
        }
        if (east) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.x == Direction.EAST) {
                    directions.remove(quadrant.z.opposite)
                }
            }
        }
        return buildMap {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                put(
                    quadrant, ComputeVoxel.create(
                        context.position, quadrant,
                        ComputeVoxel.Connectivity.from(*directions.toTypedArray()),
                    )
                )
            }
        }
    }
}

object StairVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is StairBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val shape = context.blockState.getValue(BlockStateProperties.STAIRS_SHAPE)
        val half = context.blockState.getValue(BlockStateProperties.HALF)

        val left = facing.counterClockWise
        val right = facing.clockWise

        val solidParts = mutableSetOf<Quadrant>()

        when (half) {
            Half.TOP -> {
                solidParts.addAll(Quadrant.Regions.UP)
            }

            Half.BOTTOM -> {
                solidParts.addAll(Quadrant.Regions.DOWN)
            }
        }

        when (shape) {
            StairsShape.STRAIGHT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing))
            }

            StairsShape.INNER_LEFT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing))
                solidParts.addAll(Quadrant.Regions.fromDirection(left))
            }

            StairsShape.INNER_RIGHT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing))
                solidParts.addAll(Quadrant.Regions.fromDirection(right))
            }

            StairsShape.OUTER_LEFT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing, left))
            }

            StairsShape.OUTER_RIGHT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing, right))
            }
        }

        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (solidParts.contains(it)) {
                    ComputeVoxel.Connectivity.NONE
                } else {
                    ComputeVoxel.Connectivity.ALL
                }
                put(it, ComputeVoxel.create(context.position, it, connectivity))
            }
        }
    }
}

object BedVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is BedBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.position, it,
                        ComputeVoxel.Connectivity.exclude(it.y.opposite),
                    )
                )
            }
        }
    }
}

object FenceGateVoxelBlock : VoxelBlock {
    // Fence is handled by CrossCollisionVoxelBlock
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is FenceGateBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val opened = context.blockState.getValue(BlockStateProperties.OPEN)
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        val axis = facing.axis

        val getConnectivity = { quadrant: Quadrant ->
            if (axis == Direction.Axis.X) {
                ComputeVoxel.Connectivity.exclude(quadrant.x.opposite)
            } else {
                ComputeVoxel.Connectivity.exclude(quadrant.z.opposite)
            }
        }

        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (opened) {
                    ComputeVoxel.Connectivity.ALL
                } else {
                    getConnectivity(it)
                }

                put(
                    it, ComputeVoxel.create(context.position, it, connectivity)
                )
            }
        }
    }
}

object ChessVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is AbstractChestBlock<*>
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val type = context.blockState.getValue(BlockStateProperties.CHEST_TYPE)
        val blockingSide = getBlockingSide(facing, type)

        return buildMap {
            Quadrant.entries.forEach { quadrant ->
                val excludeList = buildList {
                    add(quadrant.x.opposite)
                    add(quadrant.y.opposite)
                    add(quadrant.z.opposite)
                    if (quadrant.x == blockingSide) {
                        add(quadrant.x)
                    }
                    if (quadrant.z == blockingSide) {
                        add(quadrant.z)
                    }
                }
                val connectivity = ComputeVoxel.Connectivity.exclude(*excludeList.toTypedArray())
                put(quadrant, ComputeVoxel.create(context.position, quadrant, connectivity))
            }
        }
    }

    private fun getBlockingSide(facing: Direction, type: ChestType): Direction? {
        return when (type) {
            ChestType.SINGLE -> null

            ChestType.LEFT -> {
                facing.counterClockWise
            }

            ChestType.RIGHT -> {
                facing.clockWise
            }
        }
    }
}