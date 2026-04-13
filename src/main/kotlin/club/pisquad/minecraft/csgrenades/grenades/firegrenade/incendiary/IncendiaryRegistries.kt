package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item


object IncendiaryRegistries :
    CounterStrikeGrenadeRegistries<IncendiaryEntity, IncendiaryItem, IncendiaryDamageTypes, IncendiarySoundEvents, IncendiaryConfig>(
        GrenadeType.INCENDIARY,
        IncendiaryDamageTypes,
        IncendiarySoundEvents,
        IncendiaryConfig,
        ::IncendiaryEntity,
        { IncendiaryItem(Item.Properties()) })

object IncendiaryDamageTypes : GrenadeEntityDamageTypes {
    val fire = ModDamageTypes.registerSingle("incendiary.fire")
    override val hit = ModDamageTypes.registerSingle("incendiary.hit")
    override val main = fire
}

object IncendiarySoundEvents : GrenadeSoundEvents {
    override val hitBlock = GrenadeSoundData.create("incendiary.hit_block")
    val detonate = GrenadeSoundData.create("incendiary.detonate")
    val detonateDistant = GrenadeSoundData.create("incendiary.detonate_distant")
    val detonateAir = GrenadeSoundData.create("incendiary.detonate_air")
    override val draw = GrenadeSoundData.create("incendiary.draw")
    override val pinPull = GrenadeSoundData.create("incendiary.pinpull")
    override val pinPullStart = GrenadeSoundData.create("incendiary.pinpull_start")
    val pop = GrenadeSoundData.create("incendiary.pop")
    override val `throw` = GrenadeSoundData.create("incendiary.throw")
}