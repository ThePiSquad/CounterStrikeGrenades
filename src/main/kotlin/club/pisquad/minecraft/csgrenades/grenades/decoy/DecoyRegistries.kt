package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistries
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item

object DecoyRegistries :
    CounterStrikeGrenadeRegistries<DecoyGrenadeEntity, DecoyGrenadeItem, DecoyDamageTypes, DecoySoundEvents, DecoyConfig>(
        GrenadeType.DECOY,
        DecoyDamageTypes,
        DecoySoundEvents,
        DecoyConfig,
        ::DecoyGrenadeEntity,
        { DecoyGrenadeItem(Item.Properties()) })

object DecoyDamageTypes : GrenadeEntityDamageTypes {
    val explosion = ModDamageTypes.registerSingle("decoy.explosion")
    override val hit = ModDamageTypes.registerSingle("decoy.hit")
    override val main = explosion
}

object DecoySoundEvents : GrenadeSoundEvents {
    override val draw = GrenadeSoundData.createDraw("decoy.draw")
    override val `throw` = GrenadeSoundData.createThrow("decoy.throw")
    override val hitBlock = FlashbangRegistries.sounds.hitBlock
    override val pinPull = GrenadeSoundData.create("decoy.pinpull")
    override val pinPullStart = GrenadeSoundData.create("decoy.pinpull_start")
}