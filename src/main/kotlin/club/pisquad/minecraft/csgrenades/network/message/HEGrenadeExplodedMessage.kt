package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.helper.HEGrenadeExplosionData
import club.pisquad.minecraft.csgrenades.helper.HEGrenadeRenderHelper
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

//private val Logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":message:flashbangExplodedMessage")

@Serializable
class HEGrenadeExplodedMessage(
    val entityId: Int,
    @Serializable(with = Vec3Serializer::class) val position: Vec3
) {
    companion object {

        fun encoder(msg: HEGrenadeExplodedMessage, buffer: FriendlyByteBuf) {
//            Logger.info("Encoding message $msg")
            buffer.writeUtf(Json.encodeToString(msg))
        }

        fun decoder(buffer: FriendlyByteBuf): HEGrenadeExplodedMessage {
            val text = buffer.readUtf()
//            Logger.info("Decoding string $text")
            return Json.decodeFromString<HEGrenadeExplodedMessage>(text)
        }

        fun handler(msg: HEGrenadeExplodedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true
            if (!context.direction.receptionSide.isClient) {
                return
            }
            HEGrenadeRenderHelper.render(HEGrenadeExplosionData(msg.position))

        }

    }
}