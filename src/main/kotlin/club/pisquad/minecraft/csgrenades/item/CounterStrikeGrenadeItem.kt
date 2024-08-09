package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

open class CounterStrikeGrenadeItem(properties: Properties) : Item(properties) {
    private var isHoldingBefore: Boolean = false

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide) return

        if (isSelected) {
            if (!isHoldingBefore) {
                entity.playSound(ModSoundEvents.FLASHBANG_DRAW.get(), 1.0f, 1.0f)
                isHoldingBefore = true
            }
        } else {
            isHoldingBefore = false
        }
    }
}