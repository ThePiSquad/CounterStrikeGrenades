package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.serializer.RotationSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.Rotations
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
class GrenadeThrownMessage(
    @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
    val speed: Double,
    val grenadeType: GrenadeType,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = RotationSerializer::class) val rotation: Rotations,
    val customSound: String? = null,
) {
    companion object {

        fun encoder(msg: GrenadeThrownMessage, buffer: FriendlyByteBuf) {
            buffer.writeUtf(Json.encodeToString(msg))
        }

        fun decoder(buffer: FriendlyByteBuf): GrenadeThrownMessage {
            val text = buffer.readUtf()
            return Json.decodeFromString<GrenadeThrownMessage>(text)
        }

        fun handler(msg: GrenadeThrownMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            val player: ServerPlayer = context.sender ?: return

            context.enqueueWork {

                val serverLevel: ServerLevel = player.level() as ServerLevel

                val entityType = when (msg.grenadeType) {
                    GrenadeType.FLASH_BANG -> ModEntities.FLASH_BANG_ENTITY.get()
                    GrenadeType.SMOKE_GRENADE -> ModEntities.SMOKE_GRENADE_ENTITY.get()
                    GrenadeType.HEGRENADE -> ModEntities.HEGRENADE_ENTITY.get()
                    GrenadeType.INCENDIARY -> ModEntities.INCENDIARY_ENTITY.get()
                    GrenadeType.MOLOTOV -> ModEntities.MOLOTOV_ENTITY.get()
                    GrenadeType.DECOY_GRENADE -> ModEntities.DECOY_GRENADE_ENTITY.get()
                }

                val grenadeEntity = entityType.create(serverLevel) ?: return@enqueueWork
                grenadeEntity.owner = context.sender?.level()?.getPlayerByUUID(msg.ownerUUID)

                if (grenadeEntity is club.pisquad.minecraft.csgrenades.entity.DecoyGrenadeEntity) {
                    grenadeEntity.findAndSetTaczGunIdOnThrow()
                    if (!msg.customSound.isNullOrBlank()) {
                        grenadeEntity.setCustomSound(msg.customSound)
                    }
                }

                grenadeEntity.setPos(msg.position)
                grenadeEntity.shootFromRotation(
                    player,
                    msg.rotation.x,
                    msg.rotation.y,
                    0.0f, // Roll is not used
                    msg.speed.toFloat(),
                    0f,
                )

                serverLevel.addFreshEntity(grenadeEntity)

                context.packetHandled = true
                if (!player.isCreative) {
                    player.getItemInHand(InteractionHand.MAIN_HAND).count -= 1
                }
            }
        }
    }
}
