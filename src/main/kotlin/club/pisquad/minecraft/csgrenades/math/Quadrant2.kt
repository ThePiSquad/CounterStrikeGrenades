package club.pisquad.minecraft.csgrenades.math

//
//enum class Quadrant2(val z: Direction, val x: Direction) {
//    NE(Direction.NORTH, Direction.EAST),
//    NW(Direction.NORTH, Direction.WEST),
//    SE(Direction.SOUTH, Direction.EAST),
//    SW(Direction.SOUTH, Direction.WEST);
//
//    companion object {
//        fun fromPosition(position: Vec3): Quadrant2 {
//            return when (Pair(position.z > 0, position.x > 0)) {
//                Pair(true, true) -> {
//                    NE
//                }
//
//                Pair(true, false) -> {
//                    NW
//                }
//
//                Pair(false, true) -> {
//                    SE
//                }
//
//                else -> {
//                    SW
//                }
//            }
//        }
//
//        fun contactWithDirection(direction: Direction): EnumSet<Quadrant2> {
//            val c = when (direction) {
//                Direction.DOWN, Direction.UP -> {
//                    Quadrant2.entries
//                }
//
//                Direction.NORTH -> {
//                    listOf(
//                        NE, NW
//                    )
//                }
//
//                Direction.SOUTH -> {
//                    listOf(
//                        SE, SW
//                    )
//                }
//
//                Direction.WEST -> {
//                    listOf(
//                        NW, SW
//                    )
//                }
//
//                Direction.EAST -> {
//                    listOf(
//                        NE, SE
//                    )
//                }
//            }
//            return EnumSet.copyOf(c)
//        }
//
//    }
//}