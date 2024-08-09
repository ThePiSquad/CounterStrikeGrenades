package club.pisquad.minecraft.csgrenades.helper

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.security.InvalidParameterException


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object TickHelper {
    private var isServerSide = false


    private val tickCounts = mutableMapOf<String, Int>()

    fun create(name: String) {
        if (!tickCounts.containsKey(name)) {
            tickCounts[name] = 0
        }
    }

    fun reset(name: String) {
        if (tickCounts.containsKey(name)) tickCounts[name] = 0 else throw InvalidParameterException()
    }

    fun get(name: String): Int {
        return tickCounts[name] ?: throw InvalidParameterException()
    }

    @SubscribeEvent
    fun tickHandler(event: TickEvent) {
        if(event.phase==TickEvent.Phase.END)return

        if (!isServerSide && event is TickEvent.ClientTickEvent) {
            for (key in tickCounts.keys) {
                tickCounts[key] = tickCounts[key]!!.plus(1)
            }
            return
        } else if (
            event is TickEvent.ServerTickEvent
        ) {
            for (key in tickCounts.keys) {
                tickCounts[key] = tickCounts[key]!!.plus(1)
            }
            isServerSide = true
        }

    }
}