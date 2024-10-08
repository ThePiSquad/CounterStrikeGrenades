package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.STRONG_THROW_SPEED
import club.pisquad.minecraft.csgrenades.WEAK_THROW_SPEED
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registery.ModEntities
import club.pisquad.minecraft.csgrenades.serializer.RotationSerializer
import club.pisquad.minecraft.csgrenades.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.Rotations
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
enum class GrenadeThrowType(val speed: Double) {
    Strong(STRONG_THROW_SPEED),
    Weak(WEAK_THROW_SPEED)
}

@Serializable
class GrenadeThrownMessage(
    @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
    val speed: Double,
    val grenadeType: GrenadeType,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = RotationSerializer::class) val rotation: Rotations,
) {
    companion object {
//        private val Logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":message:grenadeThrownMessage")

        fun encoder(msg: GrenadeThrownMessage, buffer: FriendlyByteBuf) {
//            Logger.info("Encoding message $msg")
            buffer.writeUtf(Json.encodeToString(msg))
        }

        fun decoder(buffer: FriendlyByteBuf): GrenadeThrownMessage {
            val text = buffer.readUtf()
//            Logger.info("Decoding string $text")
            return Json.decodeFromString<GrenadeThrownMessage>(text)
        }

        fun handler(msg: GrenadeThrownMessage, ctx: Supplier<NetworkEvent.Context>) {
//            Logger.info("Handling message $msg")

            val context = ctx.get()
            val sender: ServerPlayer = context.sender
                ?: //                Logger.debug("Handling message failed because the sender is null")
                return

            val serverLevel: ServerLevel = sender.level() as ServerLevel

            val entityType = when (msg.grenadeType) {
                GrenadeType.FLASH_BANG -> ModEntities.FLASH_BANG_ENTITY.get()
                GrenadeType.SMOKE_GRENADE -> ModEntities.SMOKE_GRENADE_ENTITY.get()
                GrenadeType.HEGRENADE -> ModEntities.HEGRENADE_ENTITY.get()
                GrenadeType.INCENDIARY -> ModEntities.INCENDIARY_ENTITY.get()
            }

            val grenadeEntity = entityType.create(serverLevel) ?: return
            grenadeEntity.owner = context.sender?.level()?.getPlayerByUUID(msg.ownerUUID)

            grenadeEntity.setPos(msg.position)
            grenadeEntity.shootFromRotation(
                sender,
                msg.rotation.x,
                msg.rotation.y,
                msg.rotation.z,
                msg.speed.toFloat(),
                0f
            )

            serverLevel.addFreshEntity(grenadeEntity)

            context.packetHandled = true
        }

    }


}
