package club.pisquad.minecraft.csgrenades

import net.minecraft.core.Vec3i
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
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

fun getRandomLocationFromCircle(center: Vec2, radius: Double): Vec2 {
    val randomSource = RandomSource.create()
    while (true) {
        val posDelta = Vec2(
            (randomSource.nextDouble() * radius * 2 - radius).toFloat(),
            (randomSource.nextDouble() * radius * 2 - radius).toFloat()
        )
        if (posDelta.length() < radius) {
            return center.add(posDelta)
        }
    }
}

fun getFireExtinguishRange(center: Vec3): AABB {
    return AABB(
        center.x - FIRE_EXTINGUISH_RANGE,
        center.y - FIRE_EXTINGUISH_RANGE,
        center.z - FIRE_EXTINGUISH_RANGE,
        center.x + FIRE_EXTINGUISH_RANGE,
        center.y + FIRE_EXTINGUISH_RANGE,
        center.z + FIRE_EXTINGUISH_RANGE,
    )
}