package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeConfig
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import club.pisquad.minecraft.csgrenades.math.Quadrant
import club.pisquad.minecraft.csgrenades.minus
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

object SmokeShapeHelper {

    fun baseShapeSize(): Int {
        val voxels = buildSet {
            val blocks = getAllPossibleBlocks(Vec3.ZERO)
            blocks.forEach { blockPos ->
                Quadrant.entries.forEach { quadrant ->
                    val voxelPos = VoxelPos.fromBlockAndQuadrant(blockPos, quadrant)
                    add(voxelPos)
                }
            }
        }
        return voxels.filter { isInsideBaseShape(Vec3.ZERO, it.center) }.size
    }

    fun centerLevelSize(): Int {
        val voxels = buildSet {
            val blocks = getAllPossibleBlocks(Vec3.ZERO)
            blocks.forEach { blockPos ->
                Quadrant.entries.forEach { quadrant ->
                    val voxelPos = VoxelPos.fromBlockAndQuadrant(blockPos, quadrant)
                    if (voxelPos.y == 0) {
                        add(voxelPos)
                    }
                }
            }
        }
        return voxels.filter { isInsideBaseShape(Vec3.ZERO, it.center) }.size
    }

    //    fun isInsideBaseShape(center: Vec3, position: Vec3, delta: Double = 1.0): Boolean {
//        val width = SmokeGrenadeConfig.spread.smokeWidth.get()
//        return position.distanceToSqr(center) < width.pow(2)
//    }
    fun isInsideBaseShape(center: Vec3, position: Vec3, delta: Double = 1.0): Boolean {
        val relativePos = position.minus(center)

        val c = getHalfFocalDistance(delta)
        val a = SmokeGrenadeConfig.spread.smokeWidth.get().times(delta)
        val axis = Vec2(relativePos.x.toFloat(), relativePos.z.toFloat()).normalized()
        val f1 = axis.scale(c.toFloat())
        val focus1 = Vec3(f1.x.toDouble(), 0.0, f1.y.toDouble())
        val f2 = axis.scale(-c.toFloat())
        val focus2 = Vec3(f2.x.toDouble(), 0.0, f2.y.toDouble())

        return (relativePos.distanceTo(focus1) + relativePos.distanceTo(focus2)) < 2 * a
    }

    fun getHalfFocalDistance(delta: Double = 1.0): Double {
        val width = SmokeGrenadeConfig.spread.smokeWidth.get().times(delta)
        val height = SmokeGrenadeConfig.spread.smokeHeight.get().times(delta)
        return sqrt(width.pow(2) - height.pow(2))
    }

    fun getAllPossibleBlocks(center: Vec3): List<BlockPos> {
        val width = SmokeGrenadeConfig.spread.smokeWidth.get()
        val height = SmokeGrenadeConfig.spread.smokeHeight.get()
        val maxFall = SmokeGrenadeConfig.spread.maxFall.get()

        val widthMultiplier: Int = 2
        val heightMultiplier: Int = 2

        return buildList {
            for (x in floor(center.x - width * widthMultiplier).toInt()..ceil(center.x + width * widthMultiplier).toInt()) {
                for (z in floor(center.z - width * widthMultiplier).toInt()..ceil(center.z + width * widthMultiplier).toInt()) {
                    for (y in floor(center.y - (height * heightMultiplier) - maxFall).toInt()..ceil(center.y + height * heightMultiplier).toInt()) {
                        add(BlockPos(x, y, z))
                    }
                }
            }
        }
    }
}