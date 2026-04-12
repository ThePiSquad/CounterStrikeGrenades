package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.EntityLeaveLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(
    modid = CounterStrikeGrenades.ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER],
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
object GrenadeEntityTracker {

    private val entities: MutableMap<Int, MutableMap<GrenadeType, MutableSet<CounterStrikeGrenadeEntity>>> =
        mutableMapOf()

    @JvmStatic
    @SubscribeEvent
    fun trackEntity(event: EntityJoinLevelEvent) {
        val entity = event.entity
        if (entity !is CounterStrikeGrenadeEntity) {
            return
        }
        entities.getOrPut(event.level.dimension().hashCode()) { mutableMapOf() }.getOrPut(
            entity.grenadeType
        ) { mutableSetOf() }.add(entity)
    }

    @JvmStatic
    @SubscribeEvent
    fun unTrackEntity(event: EntityLeaveLevelEvent) {
        val entity = event.entity
        if (entity !is CounterStrikeGrenadeEntity) {
            return
        }
        val result = entities[event.level.dimension().hashCode()]?.get(entity.grenadeType)?.remove(entity) ?: false
        if (result) {
            ModLogger.error(entity) { "Failed to remove this entity from GrenadeEntityTracker, it might not be properly tracked" }
        }
    }

    fun get(dimension: ResourceKey<Level>, type: GrenadeType): Set<CounterStrikeGrenadeEntity> {
        return entities[dimension.hashCode()]?.get(type) ?: emptySet()
    }

    fun get(type: GrenadeType): Set<CounterStrikeGrenadeEntity> {
        return buildSet {
            entities.forEach { (_, collection) ->
                collection.forEach { (gType, entities) ->
                    if (type == gType) {
                        addAll(entities)
                    }
                }
            }
        }
    }
}