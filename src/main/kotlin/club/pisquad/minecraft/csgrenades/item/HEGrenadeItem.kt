package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents

class HEGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {

    init {
        drawSound = ModSoundEvents.HEGRENADE_DRAW.get()
    }

}