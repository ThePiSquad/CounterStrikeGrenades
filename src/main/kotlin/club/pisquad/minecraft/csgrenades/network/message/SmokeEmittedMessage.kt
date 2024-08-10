package club.pisquad.minecraft.csgrenades.network.message

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
import kotlin.math.sqrt

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
            spawnParticles(level, msg.position)

        }

    }
}

private fun spawnParticles(level: ClientLevel, pos: Vec3) {
//    level.addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
    val particleData =
        Minecraft.getInstance().particleEngine.createParticle(
            ParticleTypes.CAMPFIRE_COSY_SMOKE,
            pos.x,
            pos.y,
            pos.z,
            0.0,
            0.0,
            0.0
        )
            ?.scale(27.4f) ?: return
    particleData.lifetime = 18 * 20

    for (i in 1..SMOKE_GRENADE_RADIUS) {
        val particleRadius = sqrt((SMOKE_GRENADE_RADIUS * SMOKE_GRENADE_RADIUS - i * i).toDouble()).toFloat()
        createParticleHelper(particleRadius, i, pos)

//        Minecraft.getInstance().particleEngine.createParticle(
//            ParticleTypes.CAMPFIRE_COSY_SMOKE,
//            pos.x,
//            pos.y,
//            pos.z,
//            0.0,
//            0.0,
//            0.0
//        )!!.scale((particleWidth / 2f).toFloat()).lifetime = 18 * 20

    }
}

private fun createParticleHelper(particleRadius: Float, distance: Int, center: Vec3) {
    val lifeTime = 18 * 20

    val particleEngine = Minecraft.getInstance().particleEngine

    particleEngine.createParticle(
        ParticleTypes.CAMPFIRE_COSY_SMOKE,
        center.x + distance,
        center.y,
        center.z,
        0.0,
        0.0,
        0.0,
    )!!.scale((particleRadius) / 0.2f).lifetime = lifeTime

    particleEngine.createParticle(
        ParticleTypes.CAMPFIRE_COSY_SMOKE,
        center.x - distance,
        center.y,
        center.z,
        0.0,
        0.0,
        0.0,
    )!!.scale((particleRadius) / 0.2f).lifetime = lifeTime

    particleEngine.createParticle(
        ParticleTypes.CAMPFIRE_COSY_SMOKE,
        center.x,
        center.y + distance,
        center.z,
        0.0,
        0.0,
        0.0,
    )!!.scale((particleRadius) / 0.2f).lifetime = lifeTime

    particleEngine.createParticle(
        ParticleTypes.CAMPFIRE_COSY_SMOKE,
        center.x,
        center.y - distance,
        center.z,
        0.0,
        0.0,
        0.0,
    )!!.scale((particleRadius) / 0.2f).lifetime = lifeTime

    particleEngine.createParticle(
        ParticleTypes.CAMPFIRE_COSY_SMOKE,
        center.x,
        center.y,
        center.z + distance,
        0.0,
        0.0,
        0.0,
    )!!.scale((particleRadius) / 0.2f).lifetime = lifeTime

    particleEngine.createParticle(
        ParticleTypes.CAMPFIRE_COSY_SMOKE,
        center.x,
        center.y,
        center.z - distance,
        0.0,
        0.0,
        0.0,
    )!!.scale((particleRadius) / 0.2f).lifetime = lifeTime

}