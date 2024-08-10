package club.pisquad.minecraft.csgrenades.network

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.network.message.FlashBangExplodedMessage
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
import club.pisquad.minecraft.csgrenades.network.message.SmokeEmittedMessage
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import java.util.*
import java.util.function.Supplier

private const val PROTOCOL_VERSION = "1"

object CsGrenadePacketHandler {
    //    val Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":packet_handler")
    private var messageTypeCount: Int = 1
        get() {
            field += 1
            return field
        }

    val INSTANCE: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(CounterStrikeGrenades.ID, "event"), { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    )

    fun handle(message: GrenadeThrownMessage, ctx: Supplier<NetworkEvent.Context>) {
//        Logger.info("Received message $message")

    }

    @Suppress("INACCESSIBLE_TYPE")
    fun registerMessage() {
        INSTANCE.registerMessage(
            messageTypeCount,
            GrenadeThrownMessage::class.java,
            GrenadeThrownMessage::encoder,
            GrenadeThrownMessage::decoder,
            GrenadeThrownMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_SERVER)
        )
        INSTANCE.registerMessage(
            messageTypeCount,
            FlashBangExplodedMessage::class.java,
            FlashBangExplodedMessage::encoder,
            FlashBangExplodedMessage::decoder,
            FlashBangExplodedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
        INSTANCE.registerMessage(
            messageTypeCount,
            SmokeEmittedMessage::class.java,
            SmokeEmittedMessage::encoder,
            SmokeEmittedMessage::decoder,
            SmokeEmittedMessage::handler,
            Optional.of(
                NetworkDirection.PLAY_TO_CLIENT
            )
        )
    }
}