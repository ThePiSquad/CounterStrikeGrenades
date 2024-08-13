package club.pisquad.minecraft.csgrenades.helper

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.INCENDIARY_LIFETIME
import club.pisquad.minecraft.csgrenades.INCENDIARY_PARTICLE_DENSITY
import club.pisquad.minecraft.csgrenades.INCENDIARY_PARTICLE_LIFETIME
import club.pisquad.minecraft.csgrenades.INCENDIARY_RANGE
import club.pisquad.minecraft.csgrenades.SoundTypes
import club.pisquad.minecraft.csgrenades.SoundUtils
import club.pisquad.minecraft.csgrenades.getRandomLocationFromCircle
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.network.message.IncendiaryExplodedMessage
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec2
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object IncendiaryRenderHelper {
    private val renderers: MutableList<IncendiaryRenderer> = mutableListOf()
    private val lock = ReentrantLock()

    fun render(data: IncendiaryExplodedMessage) {
        lock.withLock { renderers.add(IncendiaryRenderer(data)) }
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) return

        val shouldRemove = mutableListOf<IncendiaryRenderer>()
        renderers.forEach { if (it.update()) shouldRemove.add(it) }
        shouldRemove.forEach { renderers.remove(it) }
    }

}

private class IncendiaryRenderer(
    val data: IncendiaryExplodedMessage
) {
    val soundInstance: SoundInstance
    var tickCount = 0

    init {
        val player = Minecraft.getInstance().player!!

        soundInstance = EntityBoundSoundInstance(
            ModSoundEvents.INCENDIARY_EXPLODE.get(),
            SoundSource.AMBIENT,
            SoundUtils.getVolumeFromDistance(player.position().distanceTo(data.position), SoundTypes.INCENDIARY_EXPLODE)
                .toFloat(),
            1f,
            Minecraft.getInstance().level?.getEntity(data.entityId) ?: player, // don't know why im doing this
            0
        )
    }

    fun update(): Boolean {

        // Check if the fire is extinguished by smoke grenade
        if (Minecraft.getInstance().level?.getEntity(data.entityId) == null) {
            playExtinguishSound()
            return true
        }

        // Sounds
        when {
            this.data.extinguished -> playExtinguishSound()
            tickCount == 0 -> playExplosionSound()//Minecraft.getInstance().soundManager.play(soundInstance)
        }

        if (getTimeFromTickCount(tickCount.toDouble()) > INCENDIARY_LIFETIME) {
            return true
        }
        drawParticles()

        tickCount++

        return false
    }

    private fun drawParticles() {
        val particleEngine = Minecraft.getInstance().particleEngine
        val particleCount = (INCENDIARY_RANGE * INCENDIARY_RANGE * INCENDIARY_PARTICLE_DENSITY).toInt()

        for (i in 0..particleCount) {
            val pos = getRandomLocationFromCircle(
                Vec2(data.position.x.toFloat(), data.position.z.toFloat()),
                INCENDIARY_RANGE
            )
            val distance = Vec2(
                data.position.x.minus(pos.x).toFloat(),
                data.position.z.minus(pos.y).toFloat()
            ).length().toDouble()
            particleEngine.createParticle(
                ParticleTypes.SMALL_FLAME,
                pos.x.toDouble(),
                data.position.y,
                pos.y.toDouble(),
                0.0,
                0.1,
                0.0
            )?.lifetime = getLifetimeFromDistance(distance)
        }
    }

    private fun playExplosionSound() {
        val distance = data.position.distanceTo(Minecraft.getInstance().player!!.position())
        val randomSource = RandomSource.create()
        val extinguishSoundInstance = SimpleSoundInstance(
            ModSoundEvents.INCENDIARY_EXPLODE.get(),
            SoundSource.AMBIENT,
            SoundUtils.getVolumeFromDistance(distance, SoundTypes.INCENDIARY_EXPLODE).toFloat(),
            1f,
            randomSource,
            data.position.x,
            data.position.y,
            data.position.z
        )
        Minecraft.getInstance().soundManager.play(extinguishSoundInstance)
    }

    private fun playExtinguishSound() {
        val distance = data.position.distanceTo(Minecraft.getInstance().player!!.position())
        val randomSource = RandomSource.create()
        val extinguishSoundInstance = SimpleSoundInstance(
            ModSoundEvents.INCENDIARY_POP.get(),
            SoundSource.AMBIENT,
            SoundUtils.getVolumeFromDistance(distance, SoundTypes.INCENDIARY_POP).toFloat(),
            0.8f,
            randomSource,
            data.position.x,
            data.position.y,
            data.position.z
        )
        Minecraft.getInstance().soundManager.play(extinguishSoundInstance)
    }
}

fun getLifetimeFromDistance(distance: Double): Int {
    val randomSource = RandomSource.create()
    return (INCENDIARY_RANGE - distance).div(INCENDIARY_LIFETIME).times(INCENDIARY_PARTICLE_LIFETIME)
        .toInt() + randomSource.nextInt(0, 5)
}
