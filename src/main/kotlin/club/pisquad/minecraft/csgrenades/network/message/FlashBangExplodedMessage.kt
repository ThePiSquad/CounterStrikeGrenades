package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.render.FlashBangEffect
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3i
import java.util.function.Supplier
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min

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
            val player = Minecraft.getInstance().player ?: return
            val newValue =
                calculateFlashbangEffectInitialValue(
                    player.position().plus(Vec3(0.0, 1.62, 0.0)),
                    msg.position,
                    player.lookAngle,
                    player.level()
                )
            FlashBangEffect.render(newValue)


//            val context = ctx.get()
//            context.direction.toString()
//            Logger.info("Handling message $msg")
        }

    }
}

private enum class FlashBangBlockingType {
    NO_BLOCKING,
    FULLY_BLOCKING,
    PARTIAL_BLOCKING, // If the block is a transparent block
}

private fun getFlashBangBlockingType(blockState: BlockState): FlashBangBlockingType {
    if (blockState.isAir) {
        return FlashBangBlockingType.NO_BLOCKING
    }
    return if (blockState.canOcclude()) FlashBangBlockingType.FULLY_BLOCKING else FlashBangBlockingType.PARTIAL_BLOCKING
}


private fun calculateFlashbangEffectInitialValue(
    playerEyePos: Vec3,
    flashbangPos: Vec3,
    lookAngle: Vec3,
    level: Level
): Double {
    val distance = playerEyePos.distanceTo(flashbangPos)
    val playerToFlashBangVec = flashbangPos.minus(playerEyePos)
    val direction = playerToFlashBangVec.normalize().reverse()

    val angle = lookAngle.normalize().dot(playerToFlashBangVec.normalize()) + 1

    // Get all block between player and flashbang
    var flashbangBlockingType: FlashBangBlockingType = FlashBangBlockingType.NO_BLOCKING
    for (i in 1..playerToFlashBangVec.length().toInt()) {
        when (getFlashBangBlockingType(
            level.getBlockState(
                BlockPos(
                    flashbangPos.plus(direction.scale(i.toDouble())).toVec3i()
                )
            )
        )) {
            FlashBangBlockingType.PARTIAL_BLOCKING -> {
                flashbangBlockingType = FlashBangBlockingType.PARTIAL_BLOCKING
            }

            FlashBangBlockingType.FULLY_BLOCKING -> {
                flashbangBlockingType = FlashBangBlockingType.FULLY_BLOCKING
                break
            }

            FlashBangBlockingType.NO_BLOCKING -> {}
        }

    }

    val distanceFactor = getDistanceFactor(distance)
    val angleFactor = getAngleFactor(angle)

    var value = 2 * distanceFactor * angleFactor
    value = when (flashbangBlockingType) {
        FlashBangBlockingType.NO_BLOCKING -> value
        FlashBangBlockingType.PARTIAL_BLOCKING -> value.times(0.25)
        FlashBangBlockingType.FULLY_BLOCKING -> 0.0
    }

    return min(max(0.0, value), 1.0)
}

private fun getDistanceFactor(distance: Double): Double {
    if (distance <= 30) {
        return 1.0
    }
    return 1 - log((distance - 30 + 1).times(0.5), 5.0).times(0.5)
}

private fun getAngleFactor(angle: Double): Double {
    return log(angle + 1.0, 100.0).times(4)

}