package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.SoundTypes
import club.pisquad.minecraft.csgrenades.SoundUtils
import club.pisquad.minecraft.csgrenades.helper.SmokeRenderHelper
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

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
            val soundType =
                if (distance > 15) SoundTypes.SMOKE_GRENADE_EXPLODE_DISTANT else SoundTypes.SMOKE_GRENADE_EMIT
            val entity = level.getEntity(msg.entityId) ?: return

            val soundInstance = EntityBoundSoundInstance(
                soundEvent,
                SoundSource.AMBIENT,
                SoundUtils.getVolumeFromDistance(distance, soundType).toFloat(),
                1f,
                entity,
                0
            )
            soundManager.play(soundInstance)

            // Particles
            SmokeRenderHelper.render(Minecraft.getInstance().particleEngine, msg.position)

        }

    }
}
