package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades.Logger
import club.pisquad.minecraft.csgrenades.entity.client.render.FlashBangRenderer
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModRenderers {
    @SubscribeEvent
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        Logger.info("Registering entity renderers")

        Logger.info("Registering render for flashbang")
        event.registerEntityRenderer(
            ModEntities.FLASH_BANG_ENTITY.get(),
            ::FlashBangRenderer
        )

    }
}