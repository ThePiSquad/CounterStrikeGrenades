package club.pisquad.minecraft.csgrenades.helper

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.registery.ModParticles
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object SmokeRenderHelper {
    private var renderers: MutableList<SmokeRenderer> = mutableListOf()

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) return
        renderers.forEach() {
            it.update()

        }
        val shouldRemove: MutableList<SmokeRenderer> = mutableListOf()
        renderers.forEach() {
            if (it.done) {
                shouldRemove.add(it)
            }
        }
        shouldRemove.forEach() {
            renderers.remove(it)
        }
    }

    fun render(particleEngine: ParticleEngine, position: Vec3) {
        renderers.add(SmokeRenderer(particleEngine, position))
    }
}

class SmokeRenderer(
    private val particleEngine: ParticleEngine,
    private val center: Vec3
) {
    var done: Boolean = false
    private var tickCount = 0
    private val particlePerTick =
        SMOKE_GRENADE_PARTICLE_COUNT.div(SMOKE_GRENADE_TOTAL_GENERATION_TIME.times(20)).toInt()

    fun update() {
        val time = getTimeFromTickCount(tickCount.toDouble())
        val radius: Double = when {
            time < SMOKE_GRENADE_SPREAD_TIME -> (time.div(SMOKE_GRENADE_SPREAD_TIME).times(SMOKE_GRENADE_RADIUS)) + .1
            else -> SMOKE_GRENADE_RADIUS.toDouble()
        }
        // unify generation rate should be ok>?
        for (i in 0..particlePerTick) {
            val location = getRandomLocationFromSphere(center, radius)
            particleEngine.createParticle(
                ModParticles.SMOKE_PARTICLE.get(),
                location.x,
                location.y,
                location.z,
                0.0,
                0.0,
                0.0
            )
        }
        if (time > SMOKE_GRENADE_TOTAL_GENERATION_TIME) {
            this.done = true
            return
        }
        tickCount++
    }
}