package club.pisquad.minecraft.csgrenades.helper

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.SoundTypes
import club.pisquad.minecraft.csgrenades.SoundUtils
import club.pisquad.minecraft.csgrenades.getRandomLocationFromSphere
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

data class HEGrenadeExplosionData(
    var position: Vec3,
) {}


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object HEGrenadeRenderHelper {
    private val renderers: MutableList<HEGrenadeRenderer> = mutableListOf()

    fun render(data: HEGrenadeExplosionData) {
        renderers.add(HEGrenadeRenderer(data))
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) return

        val shouldRemove: MutableList<HEGrenadeRenderer> = mutableListOf()
        renderers.forEach() {
            if (it.update()) {
                shouldRemove.add(it)
            }
        }
        shouldRemove.forEach() {
            renderers.remove(it)
        }
    }

}

class HEGrenadeRenderer(
    private val data: HEGrenadeExplosionData
) {
    var done: Boolean = false
    private var soundInstance: SoundInstance? = null

    init {
        val player = Minecraft.getInstance().player!!
        val distance = player.position().distanceTo(data.position)

        val soundEvent =
            if (distance > 15) ModSoundEvents.HEGRENADE_EXPLODE_DISTANT.get() else ModSoundEvents.HEGRENADE_EXPLODE.get()
        val soundType = if (distance > 15) SoundTypes.HEGRENADE_EXPLODE_DISTANT else SoundTypes.HEGRENADE_EXPLODE

        this.soundInstance = SimpleSoundInstance(
            soundEvent,
            SoundSource.AMBIENT,
            SoundUtils.getVolumeFromDistance(distance, soundType).toFloat(),
            1f,
            RandomSource.create(),
            data.position.x,
            data.position.y,
            data.position.z
        )
        Minecraft.getInstance().soundManager.play(this.soundInstance!!)


        val particleEngine = Minecraft.getInstance().particleEngine
        val randomSource = RandomSource.create()

        for (i in 1..1000) {
            particleEngine.createParticle(
                ParticleTypes.SMOKE,
                data.position.x,
                data.position.y,
                data.position.z,
                randomSource.nextDouble().times(1.4) - 0.7,
                randomSource.nextDouble().times(1.4) - 0.7,
                randomSource.nextDouble().times(1.4) - 0.7,
            )?.lifetime = 10
        }
        for (i in 1..300) {
            val location = getRandomLocationFromSphere(data.position, 4.0)
            particleEngine.createParticle(
                ParticleTypes.LARGE_SMOKE,
                location.x,
                location.y,
                location.z,
                0.0,
                0.0,
                0.0,
            )?.scale(2f)?.lifetime = 20
        }
    }

    fun update(): Boolean {
        return true
    }

}