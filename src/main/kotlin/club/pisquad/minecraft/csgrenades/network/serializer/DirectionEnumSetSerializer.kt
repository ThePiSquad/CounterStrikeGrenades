package club.pisquad.minecraft.csgrenades.network.serializer
//
//import kotlinx.serialization.KSerializer
//import kotlinx.serialization.builtins.ListSerializer
//import kotlinx.serialization.builtins.serializer
//import kotlinx.serialization.descriptors.SerialDescriptor
//import kotlinx.serialization.encoding.Decoder
//import kotlinx.serialization.encoding.Encoder
//import net.minecraft.core.Direction
//import java.util.*
//
//object DirectionEnumSetSerializer : KSerializer<EnumSet<Direction>> {
//    private val delegateSerializer = ListSerializer(Int.serializer())
//
//    override val descriptor: SerialDescriptor = SerialDescriptor("DirectionEnumSet", delegateSerializer.descriptor)
//
//    override fun serialize(
//        encoder: Encoder,
//        value: EnumSet<Direction>
//    ) {
//        val ordinals = value.map { it.ordinal }
//        encoder.encodeSerializableValue(delegateSerializer, ordinals)
//    }
//
//    override fun deserialize(decoder: Decoder): EnumSet<Direction> {
//        val ordinals = decoder.decodeSerializableValue(delegateSerializer)
//        val values = ordinals.map { ordinal -> Direction.entries.find { it.ordinal == ordinal }!! }
//        return EnumSet.copyOf(values)
//
//    }
//}