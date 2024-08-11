package club.pisquad.minecraft.csgrenades

import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

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
    while (true) {
        val posDelta = Vec3(
            Random.nextDouble(0.0, radius * 2) - radius,
            Random.nextDouble(0.0, radius * 2) - radius,
            Random.nextDouble(0.0, radius * 2) - radius
        )
        if (posDelta.length() < radius) {
            return center.add(posDelta)
        }
    }
}