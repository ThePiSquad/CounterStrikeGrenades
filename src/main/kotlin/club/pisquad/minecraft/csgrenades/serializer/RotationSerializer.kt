package club.pisquad.minecraft.csgrenades.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.core.Rotations

class RotationSerializer : KSerializer<Rotations> {
    private val delegateSerializer = FloatArraySerializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("Rotations", delegateSerializer.descriptor)

    override fun deserialize(decoder: Decoder): Rotations {
        val array = decoder.decodeSerializableValue(delegateSerializer)
        return Rotations(array[0], array[1], array[2])
    }

    override fun serialize(encoder: Encoder, value: Rotations) {
        val data = floatArrayOf(
            value.x,
            value.y,
            value.z
        )
        encoder.encodeSerializableValue(delegateSerializer, data)
    }
}