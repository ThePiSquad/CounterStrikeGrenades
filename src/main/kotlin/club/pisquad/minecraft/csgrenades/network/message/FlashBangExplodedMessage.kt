package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.render.FlashBangEffect
import club.pisquad.minecraft.csgrenades.render.FlashBangEffectData
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier
import kotlin.math.PI
import kotlin.math.acos

//private val Logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":message:flashbangExplodedMessage")

@Serializable
class FlashBangExplodedMessage(
    @Serializable(with = Vec3Serializer::class) val position: Vec3
) {
    companion object {

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
            val context = ctx.get()
            context.packetHandled = true
            if (!context.direction.receptionSide.isClient) {
                return
            }

            val player = Minecraft.getInstance().player ?: return

            val playerToFlashVec = msg.position.add(player.position().reverse())


            val angle = acos(player.lookAngle.dot(playerToFlashVec.normalize())).times(180).times(1/PI)

            FlashBangEffect.render(
                FlashBangEffectData.create(
                    angle,
                    playerToFlashVec.length(),
                    msg.position,
                    player.position().add(Vec3(0.0, 1.62, 0.0))
                )
            )
        }

    }
}

private enum class FlashBangBlockingType {
    NO_BLOCKING,
    FULLY_BLOCKING,
    PARTIAL_BLOCKING,
}

private fun getFlashBangBlockingType(blockState: BlockState): FlashBangBlockingType {
    if (blockState.isAir) {
        return FlashBangBlockingType.NO_BLOCKING
    }
    return if (blockState.canOcclude()) FlashBangBlockingType.FULLY_BLOCKING else FlashBangBlockingType.PARTIAL_BLOCKING
}