package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.SmokeGrenadeEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/**
 *Since KFF is not mapping those methods correctly
 */

operator fun Vec3.minus(other: Vec3): Vec3 = this.add(other.reverse())

fun Vec3.toVec3i(): Vec3i = Vec3i(x.toInt(), y.toInt(), z.toInt())

fun Vec3.snapToAxis(): Direction {
    val absX = abs(x)
    val absY = abs(y)
    val absZ = abs(z)
    return when {
        absX >= absY && absX >= absZ -> if (x > 0) Direction.NORTH else Direction.SOUTH
        absY >= absX && absY >= absZ -> if (y > 0) Direction.UP else Direction.DOWN
        else -> if (z > 0) Direction.EAST else Direction.WEST
    }
}

fun BlockPos.toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

fun Vec3i.toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

fun getTimeFromTickCount(tickCount: Double): Double = tickCount / 20.0

fun getRandomLocationFromSphere(center: Vec3, radius: Double): Vec3 {
    val randomSource = RandomSource.createNewThreadLocalInstance()
    while (true) {
        val posDelta = Vec3(
            randomSource.nextDouble() * radius * 2 - radius,
            randomSource.nextDouble() * radius * 2 - radius,
            randomSource.nextDouble() * radius * 2 - radius,
        )
        if (posDelta.length() < radius) {
            return center.add(posDelta)
        }
    }
}

fun getRandomLocationFromCircle(center: Vec2, radius: Double): Vec2 {
    val randomSource = RandomSource.createNewThreadLocalInstance()
    while (true) {
        val posDelta = Vec2(
            (randomSource.nextDouble() * radius * 2 - radius).toFloat(),
            (randomSource.nextDouble() * radius * 2 - radius).toFloat(),
        )
        if (posDelta.length() < radius) {
            return center.add(posDelta)
        }
    }
}

fun getRandomLocationFromBlockSurface(position: BlockPos): Vec3 {
    val x = Random.nextDouble()
    val z = Random.nextDouble()
    return Vec3(position.x + x, position.y + 1.0, position.z + z)
}

fun isPositionInSmoke(level: Level, pos: Vec3): Boolean {
    val blockPos = BlockPos.containing(pos)
    return level.getEntitiesOfClass(
        SmokeGrenadeEntity::class.java,
        AABB(BlockPos(pos.toVec3i())).inflate(
            ModConfig.SmokeGrenade.SMOKE_RADIUS.get() * 2.0,
            ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get() + ModConfig.SmokeGrenade.SMOKE_RADIUS.get() * 2.0,
            ModConfig.SmokeGrenade.SMOKE_RADIUS.get() * 2.0,
        ),
    ).any {
        it.getSpreadBlocks().any { block -> block == blockPos }
    }
}

fun getBlockPosAround2D(pos: Vec3, radius: Int): List<BlockPos> {
    val posVec3 = BlockPos.containing(pos)
    val begin = posVec3.offset(-radius, 0, -radius)
    val result = mutableListOf<BlockPos>()
    repeat((radius * 2) + 1) { xOffset ->
        repeat((radius * 2) + 1) { zOffset ->
            result.add(BlockPos(begin.offset(xOffset, 0, zOffset)))
        }
    }
    return result
}

fun getBlocksAround3D(pos: Vec3, xRange: Int, yRange: Int, zRange: Int): List<BlockPos> {
    val posVec3 = BlockPos.containing(pos)
    val begin = posVec3.offset(-xRange, -yRange, -zRange)
    val result = mutableListOf<BlockPos>()
    repeat((xRange * 2) + 1) { xOffset ->
        repeat((yRange * 2) + 1) { yOffset ->
            repeat((zRange * 2) + 1) { zOffset ->
                result.add(
                    BlockPos(
                        begin.offset(
                            xOffset,
                            yOffset,
                            zOffset,
                        ),
                    ),
                )
            }
        }
    }
    return result
}

fun linearInterpolate(from: Double, to: Double, t: Double): Double = from + (to - from) * t

fun Long.millToTick(): Long = this.div(50)

fun Vec3.horizontalDistanceTo(other: Vec3): Double = sqrt(
    Vec2(this.x.toFloat(), this.z.toFloat()).distanceToSqr(Vec2(other.x.toFloat(), other.z.toFloat())).toDouble(),
)

fun BlockPos.horizontalDistanceToSqr(other: BlockPos): Double = Vec2(this.x.toFloat(), this.z.toFloat()).distanceToSqr(Vec2(other.x.toFloat(), other.z.toFloat())).toDouble()

fun BlockPos.horizontalDistanceTo(other: BlockPos): Double = sqrt(
    this.horizontalDistanceToSqr(other),
)
