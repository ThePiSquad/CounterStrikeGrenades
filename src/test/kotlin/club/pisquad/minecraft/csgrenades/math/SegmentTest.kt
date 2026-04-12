package club.pisquad.minecraft.csgrenades.math

import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SegmentTest {
    @Test
    fun intersectAabb() {
        val segment = Segment(
            Vec3(0.0, 0.5, 0.5),
            Vec3(3.0, 0.5, 0.5),
        )
        val result = segment.intersectAabb(
            AABB(
                1.0, 0.0, 0.0, 2.0, 1.0, 1.0,
            ),
        )
        assertNotNull(result)
        assertEquals(Direction.WEST, result.second)
        assertEquals(Vec3(1.0, 0.5, 0.5), result.first)
    }
}
