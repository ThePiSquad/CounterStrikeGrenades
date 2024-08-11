package club.pisquad.minecraft.csgrenades

import net.minecraft.core.Vec3i
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

/**
 *Since KFF is not mapping those methods correctly
 */

fun Vec3.toVec3i(): Vec3i {
    return Vec3i(x.toInt(), y.toInt(), z.toInt())
}

fun getTimeFromTickCount(tickCount: Double): Double {
    return tickCount / 20.0
}

fun getRandomLocationFromSphere(center: Vec3, radius: Double): Vec3 {
    val randomSource = RandomSource.create()
    while (true) {
        val posDelta = Vec3(
            randomSource.nextDouble() * radius * 2 - radius,
            randomSource.nextDouble() * radius * 2 - radius,
            randomSource.nextDouble() * radius * 2 - radius
        )
        if (posDelta.length() < radius) {
            return center.add(posDelta)
        }
    }
}