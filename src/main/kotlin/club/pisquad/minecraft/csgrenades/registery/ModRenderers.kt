package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades.Logger
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModRenderers {
    @SubscribeEvent
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        Logger.info("Registering entity renderers")

        Logger.info("Registering render for flashbang")

        EntityRenderers.register(ModEntities.FLASH_BANG_ENTITY.get(), ::ThrownItemRenderer)

    }
}