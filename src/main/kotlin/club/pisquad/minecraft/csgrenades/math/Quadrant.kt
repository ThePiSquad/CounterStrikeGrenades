package club.pisquad.minecraft.csgrenades.math

import net.minecraft.core.Direction

enum class Quadrant(val z: Direction, val x: Direction, val y: Direction) {
    NEU(Direction.NORTH, Direction.EAST, Direction.UP),
    NWU(Direction.NORTH, Direction.WEST, Direction.UP),
    SEU(Direction.SOUTH, Direction.EAST, Direction.UP),
    SWU(Direction.SOUTH, Direction.WEST, Direction.UP),
    NED(Direction.NORTH, Direction.EAST, Direction.DOWN),
    NWD(Direction.NORTH, Direction.WEST, Direction.DOWN),
    SED(Direction.SOUTH, Direction.EAST, Direction.DOWN),
    SWD(Direction.SOUTH, Direction.WEST, Direction.DOWN);

    fun oppositeY(): Quadrant {
        return Quadrant.entries.find {
            it.x == this.x && it.z == this.z && it.y == this.y.opposite
        }!!
    }

    fun oppositeX(): Quadrant {
        return Quadrant.entries.find {
            it.x.opposite == this.x && it.z == this.z && it.y == this.y
        }!!
    }

    fun oppositeZ(): Quadrant {
        return Quadrant.entries.find {
            it.x == this.x && it.z == this.z.opposite && it.y == this.y
        }!!
    }

    fun clockWise(): Quadrant {
        return Quadrant.entries.find {
            it.x == this.z.clockWise && it.z == this.x.clockWise && it.y == this.y
        }!!
    }

    fun counterClockWise(): Quadrant {
        return Quadrant.entries.find {
            it.x == this.z.counterClockWise && it.z == this.x.counterClockWise && it.y == this.y
        }!!
    }

    object Regions {
        val UP: Set<Quadrant> = Quadrant.entries.filter { it.y == Direction.UP }.toSet()
        val DOWN: Set<Quadrant> = Quadrant.entries.filter { it.y == Direction.DOWN }.toSet()
        val NORTH: Set<Quadrant> = Quadrant.entries.filter { it.z == Direction.NORTH }.toSet()
        val SOUTH: Set<Quadrant> = Quadrant.entries.filter { it.z == Direction.SOUTH }.toSet()
        val WEST: Set<Quadrant> = Quadrant.entries.filter { it.x == Direction.WEST }.toSet()
        val EAST: Set<Quadrant> = Quadrant.entries.filter { it.x == Direction.EAST }.toSet()

        fun fromDirection(direction: Direction): Set<Quadrant> {
            return when (direction) {
                Direction.DOWN -> DOWN
                Direction.UP -> UP
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.WEST -> WEST
                Direction.EAST -> EAST
            }
        }

        fun fromDirection(d1: Direction, d2: Direction): Set<Quadrant> {
            require(d1.axis.isHorizontal)
            require(d2.axis.isHorizontal)
            require(d1.axis != d2.axis)

            return Quadrant.entries.filter {
                (it.x == d1 && it.z == d2) || (it.z == d1 && it.x == d2)
            }.toSet()
        }
    }
}

//
//class QuadrantRegion(
//    vararg quadrantList: Quadrant
//) {
//    val quadrants: EnumSet<Quadrant> = EnumSet.copyOf(quadrantList.toList())
//
//    val size: Int
//        get() = quadrants.size
//
//    companion object {
//        val ALL = QuadrantRegion(*Quadrant.entries.toTypedArray())
//        val NONE = QuadrantRegion()
//
//        val UP = QuadrantRegion(Quadrant.NEU, Quadrant.NWU, Quadrant.SEU, Quadrant.SWU)
//        val DOWN = QuadrantRegion(Quadrant.NED, Quadrant.NWD, Quadrant.SED, Quadrant.SWD)
//        val NORTH = QuadrantRegion(Quadrant.NEU, Quadrant.NED, Quadrant.NWU, Quadrant.NWD)
//        val SOUTH = QuadrantRegion(Quadrant.SEU, Quadrant.SED, Quadrant.SWU, Quadrant.SWD)
//        val WEST = QuadrantRegion(Quadrant.NWU, Quadrant.NWD, Quadrant.SWU, Quadrant.SWD)
//        val EAST = QuadrantRegion(Quadrant.NEU, Quadrant.NED, Quadrant.SEU, Quadrant.SED)
//
//        fun fromDirection(direction: Direction): QuadrantRegion {
//            return when (direction) {
//                Direction.DOWN -> DOWN
//                Direction.UP -> UP
//                Direction.NORTH -> NORTH
//                Direction.SOUTH -> SOUTH
//                Direction.WEST -> WEST
//                Direction.EAST -> EAST
//            }
//        }
//
//        fun fromCrossCollision(
//            north: Boolean,
//            south: Boolean,
//            west: Boolean,
//            east: Boolean
//        ): List<QuadrantRegion> {
//            val walls = buildMap {
//                put(Direction.NORTH, north)
//                put(Direction.SOUTH, south)
//                put(Direction.WEST, west)
//                put(Direction.EAST, east)
//            }
//
//
//            val findRegion = { start: Direction ->
//
//                var wallDirection = start.clockWise
//
//                val quadrants = buildList {
//                    addAll(clockWiseFromWall(start).toList())
//                    repeat(3) {
//                        if (walls[wallDirection]!!) {
//                            return@repeat
//                        }
//                        addAll(clockWiseFromWall(wallDirection).toList())
//                        wallDirection = wallDirection.clockWise
//                    }
//                }
//                Pair(QuadrantRegion(*quadrants.toTypedArray()), wallDirection)
//
//            }
//
//            val searchStart = walls.keys.find { walls[it]!! } ?: return listOf(
//                ALL
//            )
//
//            val regions = mutableListOf<QuadrantRegion>()
//
//            var regionStart = searchStart
//
//            do {
//                val result = findRegion(regionStart)
//                regions.add(result.first)
//                regionStart = result.second
//            } while (regionStart != searchStart)
//
//            return regions
//        }
//
//        fun clockWiseFromWall(direction: Direction): QuadrantRegion {
//            require(direction.axis.isHorizontal) { "Currently only supports horizontal direction" }
//            val quadrants = when (direction) {
//                Direction.DOWN, Direction.UP -> {
//                    throw Exception()
//                }
//
//                Direction.NORTH -> {
//                    listOf(Quadrant.NEU, Quadrant.NED)
//                }
//
//                Direction.SOUTH -> {
//                    listOf(Quadrant.SWU, Quadrant.SWD)
//                }
//
//                Direction.WEST -> {
//                    listOf(Quadrant.NWU, Quadrant.NWD)
//                }
//
//                Direction.EAST -> {
//                    listOf(Quadrant.SEU, Quadrant.SED)
//                }
//            }
//            return QuadrantRegion(*quadrants.toTypedArray())
//        }
//
//        fun counterClockWiseFromWall(direction: Direction): QuadrantRegion {
//            require(direction.axis.isHorizontal) { "Currently only supports horizontal direction" }
//            val quadrants = when (direction) {
//                Direction.DOWN, Direction.UP -> {
//                    throw Exception()
//                }
//
//                Direction.NORTH -> {
//                    listOf(Quadrant.NWU, Quadrant.NWD)
//                }
//
//                Direction.SOUTH -> {
//                    listOf(Quadrant.SEU, Quadrant.SED)
//                }
//
//                Direction.WEST -> {
//                    listOf(Quadrant.SWU, Quadrant.SWD)
//                }
//
//                Direction.EAST -> {
//                    listOf(Quadrant.NEU, Quadrant.NED)
//                }
//            }
//            return QuadrantRegion(*quadrants.toTypedArray())
//        }
//    }
//
//    override fun hashCode(): Int {
//        return quadrants.hashCode()
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        other as QuadrantRegion
//        return quadrants == other.quadrants
//    }
//
//    fun contains(quadrant: Quadrant): Boolean {
//        return quadrants.contains(quadrant)
//    }
//
//    fun toList(): List<Quadrant> {
//        return quadrants.toList()
//    }
//
//    fun join(other: QuadrantRegion): QuadrantRegion {
//        val newQuadrants = this.quadrants.toMutableList()
//        newQuadrants.addAll(other.quadrants.toList())
//        return QuadrantRegion(
//            *newQuadrants.toTypedArray()
//        )
//    }
//
//    fun complement(): QuadrantRegion {
//        val quadrants = Quadrant.entries.filterNot {
//            this.quadrants.contains(it)
//        }
//        return QuadrantRegion(*quadrants.toTypedArray())
//    }
//}