package club.pisquad.minecraft.csgrenades.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.core.UUIDUtil
import java.util.*

class UUIDSerializer : KSerializer<UUID> {
    private val delegateSerializer = IntArraySerializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("UUID", delegateSerializer.descriptor)

    override fun deserialize(decoder: Decoder): UUID {
        val array = decoder.decodeSerializableValue(delegateSerializer)
        return UUIDUtil.uuidFromIntArray(array)
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeSerializableValue(delegateSerializer, UUIDUtil.uuidToIntArray(value))
    }

}