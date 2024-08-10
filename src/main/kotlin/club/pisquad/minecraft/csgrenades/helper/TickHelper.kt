package club.pisquad.minecraft.csgrenades.helper

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.security.InvalidParameterException


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object TickHelper {
    private var isServerSide = false


    private val tickCounters = mutableMapOf<String, Int>()

    fun create(name: String) {
        if (!tickCounters.containsKey(name)) {
            tickCounters[name] = 0
        }
    }

    fun reset(name: String) {
        if (tickCounters.containsKey(name)) tickCounters[name] = 0 else throw InvalidParameterException()
    }

    fun get(name: String): Int {
        return tickCounters[name] ?: throw InvalidParameterException()
    }

    fun delete(name: String) {
        if (tickCounters.containsKey(name)) {
            tickCounters.remove(name)
        }
    }

    @SubscribeEvent
    fun tickHandler(event: TickEvent) {
        if (event.phase == TickEvent.Phase.END) return

        if (!isServerSide && event is TickEvent.ClientTickEvent) {
            for (key in tickCounters.keys) {
                tickCounters[key] = tickCounters[key]!!.plus(1)
            }
            return
        } else if (
            event is TickEvent.ServerTickEvent
        ) {
            for (key in tickCounters.keys) {
                tickCounters[key] = tickCounters[key]!!.plus(1)
            }
            isServerSide = true
        }

    }
}