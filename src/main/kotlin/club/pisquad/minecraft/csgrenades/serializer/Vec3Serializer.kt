package club.pisquad.minecraft.csgrenades.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.world.phys.Vec3

class Vec3Serializer : KSerializer<Vec3> {
    private val delegateSerializer = DoubleArraySerializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("Vec3", delegateSerializer.descriptor)

    override fun deserialize(decoder: Decoder): Vec3 {
        val array = decoder.decodeSerializableValue(delegateSerializer)
        return Vec3(array[0], array[1], array[2])
    }

    override fun serialize(encoder: Encoder, value: Vec3) {
        val data = doubleArrayOf(
            value.x,
            value.y,
            value.z
        )
        encoder.encodeSerializableValue(delegateSerializer, data)
    }

}