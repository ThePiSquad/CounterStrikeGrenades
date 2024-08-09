package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents


class FlashBangItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    private var drawSound = ModSoundEvents.FLASHBANG_DRAW.get()
}
