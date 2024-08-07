package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.FLASHBANG_EFFECT_KEY
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class FlashBangExplodedMessage(
    @Serializable(with = Vec3Serializer::class) val position: Vec3
) {
    companion object {
//        private val Logger: Logger =
//            LogManager.getLogger(CounterStrikeGrenades.ID + ":message:flashbangExplodedMessage")

        fun encoder(msg: FlashBangExplodedMessage, buffer: FriendlyByteBuf) {
//            Logger.info("Encoding message $msg")
            buffer.writeUtf(Json.encodeToString(msg))
        }

        fun decoder(buffer: FriendlyByteBuf): FlashBangExplodedMessage {
            val text = buffer.readUtf()
//            Logger.info("Decoding string $text")
            return Json.decodeFromString<FlashBangExplodedMessage>(text)
        }

        fun handler(msg: FlashBangExplodedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player = Minecraft.getInstance().player ?: return
            player.persistentData.putDouble(FLASHBANG_EFFECT_KEY, 1.0)

            val context = ctx.get()
            context.direction.toString()
//            Logger.info("Handling message $msg")
        }

    }


}