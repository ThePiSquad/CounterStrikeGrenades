package club.pisquad.minecraft.csgrenades

import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3

/**
 *Since KFF is not mapping hte methods correctly
 */

fun Vec3.toVec3i(): Vec3i {
    return Vec3i(x.toInt(), y.toInt(), z.toInt())
}

