package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents

class IncendiaryItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    init {
        drawSound = ModSoundEvents.INCENDIARY_DRAW.get()
        grenadeType = GrenadeType.INCENDIARY
    }
}