package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.math.Quadrant
import club.pisquad.minecraft.csgrenades.toInt
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.floor

@Serializable
data class VoxelPos(
    val x: Int,
    val y: Int,
    val z: Int,
) {

    val up: VoxelPos
        get() = this.relative(Direction.UP)

    val down: VoxelPos
        get() = this.relative(Direction.DOWN)

    val north: VoxelPos
        get() = this.relative(Direction.NORTH)

    val south: VoxelPos
        get() = this.relative(Direction.SOUTH)

    val west: VoxelPos
        get() = this.relative(Direction.WEST)

    val east: VoxelPos
        get() = this.relative(Direction.EAST)

    val neighbors: Set<VoxelPos>
        get() {
            return setOf(
                up, down, north, south, west, east
            )
        }

    val center: Vec3
        get() = this.toWorldPos().add(CENTER_OFFSET)


    fun relative(direction: Direction, distance: Int = 1): VoxelPos {
        return when (direction) {
            Direction.DOWN -> {
                VoxelPos(this.x, this.y - distance, this.z)
            }

            Direction.UP -> {
                VoxelPos(this.x, this.y + distance, this.z)
            }

            Direction.NORTH -> {
                VoxelPos(this.x, this.y, this.z - distance)
            }

            Direction.SOUTH -> {
                VoxelPos(this.x, this.y, this.z + distance)
            }

            Direction.WEST -> {
                VoxelPos(this.x - distance, this.y, this.z)
            }

            Direction.EAST -> {
                VoxelPos(this.x + distance, this.y, this.z)
            }
        }
    }

    fun toWorldPos(): Vec3 {
        val convert = { i: Int ->
            i.div(2.0)
        }
        return Vec3(convert(this.x), convert(this.y), convert(this.z))
    }

    companion object {
        val CENTER_OFFSET = Vec3(0.25, 0.25, 0.25)

        fun containing(position: Vec3): VoxelPos {
            val convert = { d: Double ->
                floor(d.times(2.0)).toInt()
            }
            return VoxelPos(convert(position.x), convert(position.y), convert(position.z))
        }

        fun fromBlockAndQuadrant(position: BlockPos, quadrant: Quadrant): VoxelPos {
            val offsetX = (quadrant.x == Direction.EAST).toInt()
            val offsetY = (quadrant.y == Direction.UP).toInt()
            val offsetZ = (quadrant.z == Direction.SOUTH).toInt()

            return VoxelPos(
                position.x * 2 + offsetX,
                position.y * 2 + offsetY,
                position.z * 2 + offsetZ,
            )
        }

    }

}

fun BlockPos.voxelPositions(): Map<Quadrant, VoxelPos> {

    val swd = VoxelPos(
        this.x.times(2), this.y.times(2), this.z.times(2)
    )
    val swu = swd.up

    val nwd = swd.north
    val nwu = nwd.up

    val sed = swd.east
    val seu = sed.up

    val ned = sed.north
    val neu = ned.up


    return buildMap {
        put(Quadrant.SWD, swd)
        put(Quadrant.SWU, swu)
        put(Quadrant.NWD, nwd)
        put(Quadrant.NWU, nwu)
        put(Quadrant.SED, sed)
        put(Quadrant.SEU, seu)
        put(Quadrant.NED, ned)
        put(Quadrant.NEU, neu)
    }
}
