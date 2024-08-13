package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.helper.IncendiaryRenderHelper
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
class IncendiaryExplodedMessage(
    val entityId: Int,
    val extinguished: Boolean,
    val isInAir: Boolean,
    @Serializable(with = Vec3Serializer::class) val position: Vec3
) {
    companion object {

        fun encoder(msg: IncendiaryExplodedMessage, buffer: FriendlyByteBuf) {
//            Logger.info("Encoding message $msg")
            buffer.writeUtf(Json.encodeToString(msg))
        }

        fun decoder(buffer: FriendlyByteBuf): IncendiaryExplodedMessage {
            val text = buffer.readUtf()
//            Logger.info("Decoding string $text")
            return Json.decodeFromString<IncendiaryExplodedMessage>(text)
        }

        fun handler(msg: IncendiaryExplodedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true
            if (!context.direction.receptionSide.isClient) {
                return
            }

            // Flashbang effect
            IncendiaryRenderHelper.render(msg)
        }

    }
}