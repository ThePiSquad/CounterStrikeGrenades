package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.debug

object SmokeGrenadeDebugOptions {

    object Outline {
        @Volatile
        var showOutline: Boolean = true

        @Volatile
        var showAll: Boolean = false

        @Volatile
        var showSpecial: Boolean = true
    }

    object Parent {
        @Volatile
        var showParent: Boolean = true

        @Volatile
        var showAll: Boolean = true

        @Volatile
        var showSpecial: Boolean = true

        @Volatile
        var showEdges: Boolean = true
    }


}