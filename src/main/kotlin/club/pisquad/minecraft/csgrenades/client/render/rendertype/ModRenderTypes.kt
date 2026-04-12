package club.pisquad.minecraft.csgrenades.client.render.rendertype

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterShadersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(value = [Dist.CLIENT], modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModRenderTypes {
    //    val volumetricSmoke: RenderType
    @JvmStatic
    @SubscribeEvent
    fun onRegisterShader(event: RegisterShadersEvent) {

    }
}