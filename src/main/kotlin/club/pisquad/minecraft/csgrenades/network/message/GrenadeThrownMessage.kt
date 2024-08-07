package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.serializer.RotationSerializer
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.Rotations
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Supplier

@Serializable
enum class GrenadeThrownType {
    Strong,
    Weak
}

@Serializable
class GrenadeThrownMessage(
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = RotationSerializer::class) val rotations: Rotations,
    val thrownType: GrenadeThrownType,
    val grenadeType: GrenadeType,
) {
    companion object {
        private val Logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":message:grenadeThrownMessage")

        fun encoder(msg: GrenadeThrownMessage, buffer: FriendlyByteBuf) {
            Logger.info("Encoding message $msg")
            buffer.writeUtf(Json.encodeToString(msg))
        }

        fun decoder(buffer: FriendlyByteBuf): GrenadeThrownMessage {
            val text = buffer.readUtf()
            Logger.info("Decoding string $text")
            return Json.decodeFromString<GrenadeThrownMessage>(text)
        }

        fun handler(msg: GrenadeThrownMessage, ctx: Supplier<NetworkEvent.Context>) {
            Logger.info("Handling message $msg")

            val context = ctx.get()
            context.enqueueWork {
                val sender: ServerPlayer = context.sender ?: return@enqueueWork
                val serverLevel: ServerLevel = sender.level() as ServerLevel
                val arrow = EntityType.ARROW.create(serverLevel) ?: return@enqueueWork
                arrow.setPos(sender.x, sender.y + 1, sender.z)
                arrow.shootFromRotation(sender, sender.xRot, sender.yRot, 0.0f, 3f, 0f)
                serverLevel.addFreshEntity(arrow)
            }
            context.packetHandled = true
        }

    }


}
