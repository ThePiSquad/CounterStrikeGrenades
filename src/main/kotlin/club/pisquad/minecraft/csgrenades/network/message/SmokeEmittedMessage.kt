package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.SMOKE_GRENADE_PARTICLE_COUNT
import club.pisquad.minecraft.csgrenades.SMOKE_GRENADE_RADIUS
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier
import kotlin.math.*
import kotlin.random.Random

@Serializable
class SmokeEmittedMessage(
    val entityId: Int,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
) {
    companion object {

        fun encoder(msg: SmokeEmittedMessage, buffer: FriendlyByteBuf) {
//            Logger.info("Encoding message $msg")
            buffer.writeUtf(Json.encodeToString(msg))
        }

        fun decoder(buffer: FriendlyByteBuf): SmokeEmittedMessage {
            val text = buffer.readUtf()
//            Logger.info("Decoding string $text")
            return Json.decodeFromString<SmokeEmittedMessage>(text)
        }

        fun handler(msg: SmokeEmittedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true


            val player = Minecraft.getInstance().player ?: return
            val level = Minecraft.getInstance().level ?: return
            val distance = msg.position.subtract(player.position()).length()


            // Sounds
            val soundManager = Minecraft.getInstance().soundManager
            val soundEvent =
                if (distance > 15) ModSoundEvents.SMOKE_EXPLODE_DISTANT.get() else ModSoundEvents.SMOKE_EMIT.get()
            val entity = level.getEntity(msg.entityId) ?: return

            val soundInstance = EntityBoundSoundInstance(soundEvent, SoundSource.AMBIENT, 1f, 1f, entity, 0)
            soundManager.play(soundInstance)

            // Particles
            spawnSmokeParticles(level, msg.position)

        }

    }
}

private fun spawnSmokeParticles(level: ClientLevel, pos: Vec3) {
    for (i in 1..SMOKE_GRENADE_PARTICLE_COUNT) {
        // GPT, my GOD

        // Generate a random radius, theta, and phi
        val r = SMOKE_GRENADE_RADIUS * Random.nextDouble().pow(1.0 / 3.0)  // cube root to ensure uniform distribution
        val theta = Random.nextDouble(0.0, 2 * PI)  // Random angle between 0 and 2π
        val phi = acos(2 * Random.nextDouble() - 1)  // Random angle between 0 and π

        // Convert spherical coordinates to Cartesian coordinates
        val x = r * sin(phi) * cos(theta) + pos.x
        val y = r * sin(phi) * sin(theta) + pos.y
        val z = r * cos(phi) + pos.z

        Minecraft.getInstance().particleEngine.createParticle(
            ParticleTypes.CAMPFIRE_COSY_SMOKE,
            x,
            y,
            z,
            0.0,
            0.0,
            0.0
        )?.scale(2f)?.lifetime = 18 * 20
    }
}