package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.particle.SmokeParticleFactory
import net.minecraft.client.particle.SpriteSet
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModParticleFactories {
    @SubscribeEvent
    fun onRegisterParticleFactories(event: RegisterParticleProvidersEvent) {

        event.registerSpriteSet(ModParticles.SMOKE_PARTICLE.get()) { sprite: SpriteSet -> SmokeParticleFactory(sprite) }
    }
}