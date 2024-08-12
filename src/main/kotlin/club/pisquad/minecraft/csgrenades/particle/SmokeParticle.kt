package club.pisquad.minecraft.csgrenades.particle

import club.pisquad.minecraft.csgrenades.SMOKE_GRENADE_SMOKE_LIFETIME
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType

class SmokeParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xSpeed: Double,
    ySpeed: Double,
    zSpeed: Double
) : TextureSheetParticle(level, x, y, z, xSpeed, ySpeed, zSpeed) {
    init {
        this.gravity = 0f
        this.setParticleSpeed(0.0, 0.0, 0.0)
        this.lifetime = (SMOKE_GRENADE_SMOKE_LIFETIME * 20).toInt()
        this.scale(5f)
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

}

class SmokeParticleFactory(
    private val spriteSet: SpriteSet
) : ParticleProvider<SimpleParticleType> {
    override fun createParticle(
        type: SimpleParticleType,
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        xSpeed: Double,
        ySpeed: Double,
        zSpeed: Double
    ): SmokeParticle {
        val particle = SmokeParticle(level, x, y, z, 0.0, 0.0, 0.0)
        particle.pickSprite(spriteSet)
        return particle
    }
}