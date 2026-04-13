package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.DistanceSegmentedSoundData
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item


object FlashbangRegistries :
    CounterStrikeGrenadeRegistries<FlashBangEntity, FlashBangItem, FlashBangDamageTypes, FlashbangSoundEvents, FlashBangConfig>(
        GrenadeType.FLASH_BANG,
        FlashBangDamageTypes,
        FlashbangSoundEvents,
        FlashBangConfig,
        ::FlashBangEntity,
        { FlashBangItem(Item.Properties()) }
    )

object FlashBangDamageTypes : GrenadeEntityDamageTypes {
    override val hit = ModDamageTypes.registerSingle("flashbang.hit")
    override val main = hit
}

object FlashbangSoundEvents : GrenadeSoundEvents {
    override val draw = GrenadeSoundData.createDraw("flashbang.draw")
    val ring = GrenadeSoundData.create("flashbang.ring")
    val ringLoop = GrenadeSoundData.create("flashbang.ring_loop")
    override val hitBlock = GrenadeSoundData.createHitBlock("flashbang.hit_block")
    override val `throw` = GrenadeSoundData.createThrow("flashbang.throw")
    override val pinPull = GrenadeSoundData.create("flashbang.pinpull")
    override val pinPullStart = GrenadeSoundData.create("flashbang.pinpull_start")

    val explode = DistanceSegmentedSoundData.createTwoPhasedExplosion(
        GrenadeSoundData.create("flashbang.explode"),
        GrenadeSoundData.create("flashbang.explode_distant")
    )
}