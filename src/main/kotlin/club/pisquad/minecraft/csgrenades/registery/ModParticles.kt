package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModParticles {
    val PARTICLE_TYPES: DeferredRegister<ParticleType<*>> =
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CounterStrikeGrenades.ID)

    val SMOKE_PARTICLE: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("smoke_particle") { SimpleParticleType(true) }
}