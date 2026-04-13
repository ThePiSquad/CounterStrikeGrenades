package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.math.Quadrant
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import java.util.*
import java.util.function.IntFunction
import kotlin.math.max

/**
 * representing a single voxel, 1/8 of a block
 *
 * @property position voxel's position, different from BlockPos
 * @property debug Debug info
 */
@Serializable
open class Voxel(
    val position: VoxelPos,
    val debug: VoxelDebug? = null,
)

@Serializable
class VoxelMap(
    val inner: Map<VoxelPos, Voxel>
) : Map<VoxelPos, Voxel> by inner {

    val edges = lazy { this.keys.filter { this.isEdge(it) } }
    val specials = lazy {
        if (this.hasDebug.value) {
            this.filter { (pos, voxel) -> voxel.debug!!.special }.keys
        } else {
            emptyList()
        }
    }

    val hasDebug = lazy { this.values.all { it.debug != null } }

    fun isEdge(position: VoxelPos): Boolean {
        return if (this.containsKey(position)) {
            !position.neighbors.all { this.containsKey(it) }
        } else {
            false
        }
    }
}

@Serializable
class VoxelDebug(
    var special: Boolean,
    var parent: Direction?,
)


class ComputeVoxel(
    val position: VoxelPos,
    val connectivity: Connectivity,
    val special: Boolean = true,
    var intensity: Int = 0,
    val spreadDecay: Int = 1,
    var parent: Direction? = null
) {
    companion object {
        fun create(
            blockPos: BlockPos,
            quadrant: Quadrant,
            connectivity: Connectivity,
            special: Boolean = true
        ): ComputeVoxel {
            return ComputeVoxel(
                VoxelPos.fromBlockAndQuadrant(blockPos, quadrant),
                connectivity,
                special
            )
        }
    }

    class Connectivity private constructor(
        private val inner: EnumSet<Direction>
    ) : Set<Direction> by inner {

        @Deprecated("??")
        override fun <T : Any?> toArray(generator: IntFunction<Array<out T?>?>): Array<out T?>? {
            return generator.apply(0)
        }

        fun isBlocking(direction: Direction): Boolean {
            return !this.contains(direction)
        }

        companion object {
            val ALL = Connectivity(EnumSet.allOf(Direction::class.java))
            val NONE = Connectivity(EnumSet.noneOf(Direction::class.java))

            fun from(vararg directions: Direction): Connectivity {
                return Connectivity(EnumSet.copyOf(directions.toList()))
            }

            fun exclude(vararg excludes: Direction): Connectivity {
                val directions = Direction.entries.filterNot { excludes.contains(it) }
                return Connectivity(EnumSet.copyOf(directions))
            }
        }
    }

    fun triggerIntensityUpdate(direction: Direction, newIntensity: Int): Boolean {
        return if (connectivity.contains(direction) && newIntensity > intensity) {
            intensity = newIntensity
            parent = direction
            true
        } else {
            false
        }
    }

    fun neighborIntensity(direction: Direction): Int {
        return if (connectivity.contains(direction)) {
            max(this.intensity - spreadDecay, 0)
        } else {
            0
        }
    }

    fun toVoxel(): Voxel {
        return Voxel(
            this.position, VoxelDebug(
                this.special,
                this.parent
            )
        )
    }
}