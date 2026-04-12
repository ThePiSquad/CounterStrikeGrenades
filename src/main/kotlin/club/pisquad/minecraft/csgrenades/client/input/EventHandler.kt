package club.pisquad.minecraft.csgrenades.client.input

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.FORGE)
object EventHandler {
    @JvmStatic
    @SubscribeEvent
    fun onPinPullStart(event: PinPullStartEvent) {

    }

    @JvmStatic
    @SubscribeEvent
    fun onPinPull(event: PinPullEvent) {

    }

    @JvmStatic
    @SubscribeEvent
    fun onThrow(event: ThrowEvent) {

    }
}