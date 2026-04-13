package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item

object MolotovRegistries :
    CounterStrikeGrenadeRegistries<MolotovEntity, MolotovItem, MolotovDamageTypes, MolotovSoundEvents, MolotovConfig>(
        GrenadeType.MOLOTOV,
        MolotovDamageTypes,
        MolotovSoundEvents,
        MolotovConfig,
        ::MolotovEntity,
        { MolotovItem(Item.Properties()) }
    )

object MolotovDamageTypes : GrenadeEntityDamageTypes {

    val fire = ModDamageTypes.registerSingle("molotov.fire")
    override val hit = ModDamageTypes.registerSingle("molotov.hit")
    override val main = fire
}

object MolotovSoundEvents : GrenadeSoundEvents {
    val detonate = GrenadeSoundData.create("molotov.detonate")
    val detonateDistant = GrenadeSoundData.create("molotov.detonate_distant")
    val detonateAir = GrenadeSoundData.create("molotov.detonate_air")
    override val draw = GrenadeSoundData.create("molotov.draw")
    val extinguish = GrenadeSoundData.create("molotov.extinguish")
    val fireIdle = GrenadeSoundData.create("molotov.fire_idle")
    val ignite = GrenadeSoundData.create("molotov.ignite")
    val fireLoop = GrenadeSoundData.create("molotov.fire_loop")
    val fireFadeout = GrenadeSoundData.create("molotov.fire_fadeout")
    override val `throw` = GrenadeSoundData.create("molotov.throw")
    val smash = GrenadeSoundData.create("molotov.smash")
    override val hitBlock = GrenadeSoundData.create("molotov.bounce")

    override val pinPull = fireIdle
    override val pinPullStart = GrenadeSoundData.empty()
}