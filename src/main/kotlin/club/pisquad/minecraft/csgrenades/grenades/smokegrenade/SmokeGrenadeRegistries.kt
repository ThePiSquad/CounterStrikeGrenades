package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.CSGrenadeEntityDataSerializer
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.data.AttachedSmokeData
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.world.item.Item

object SmokeGrenadeRegistries :
    CounterStrikeGrenadeRegistries<
            SmokeGrenadeEntity,
            SmokeGrenadeItem,
            SmokeGrenadeDamageTypes,
            SmokeGrenadeSoundEvents, SmokeGrenadeConfig>
        (
        GrenadeType.SMOKE_GRENADE,
        SmokeGrenadeDamageTypes,
        SmokeGrenadeSoundEvents,
        SmokeGrenadeConfig,
        ::SmokeGrenadeEntity,
        { SmokeGrenadeItem(Item.Properties()) }
    ) {

    val serializers = SmokeGrenadeSerializers

    override fun registerSerializers() {
        EntityDataSerializers.registerSerializer(serializers.smokeData)
    }
}

object SmokeGrenadeDamageTypes : GrenadeEntityDamageTypes {
    override val hit = ModDamageTypes.registerSingle("smokegrenade.hit")
    override val main = hit
}

object SmokeGrenadeSoundEvents : GrenadeSoundEvents {
    val can = GrenadeSoundData.create("smokegrenade.can")
    override val draw = GrenadeSoundData.create("smokegrenade.draw")
    override val hitBlock = GrenadeSoundData.create("smokegrenade.bounce")
    override val `throw` = GrenadeSoundData.create("smokegrenade.throw")
    override val pinPull = GrenadeSoundData.create("smokegrenade.pinpull")
    override val pinPullStart = GrenadeSoundData.create("smokegrenade.pinpull_start")
    val explodeDistant = GrenadeSoundData.create("smokegrenade.explode_distant")
    val clear = GrenadeSoundData.create("smokegrenade.clear")
    val emit = GrenadeSoundData.create("smokegrenade.emit")
}

object SmokeGrenadeSerializers {
    val smokeData = CSGrenadeEntityDataSerializer(AttachedSmokeData.serializer())
}