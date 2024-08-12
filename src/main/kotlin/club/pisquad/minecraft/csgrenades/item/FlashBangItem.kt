package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents


class FlashBangItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    init {
        drawSound = ModSoundEvents.FLASHBANG_DRAW.get()
        grenadeType = GrenadeType.FLASH_BANG
    }
}
