package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.client.render.GrenadeRenderer
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModRenderers {
    @SubscribeEvent
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        EntityRenderers.register(ModEntities.FLASH_BANG_ENTITY.get(), ::GrenadeRenderer)
        EntityRenderers.register(ModEntities.SMOKE_GRENADE_ENTITY.get(), ::GrenadeRenderer)
        EntityRenderers.register(ModEntities.HEGRENADE_ENTITY.get(), ::GrenadeRenderer)
        EntityRenderers.register(ModEntities.INCENDIARY_ENTITY.get(), ::GrenadeRenderer)
        EntityRenderers.register(ModEntities.MOLOTOV_ENTITY.get(), ::GrenadeRenderer)
        EntityRenderers.register(ModEntities.DECOY_GRENADE_ENTITY.get(), ::GrenadeRenderer)
    }
}