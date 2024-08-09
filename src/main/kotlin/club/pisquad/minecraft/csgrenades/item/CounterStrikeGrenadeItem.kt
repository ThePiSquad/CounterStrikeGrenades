package club.pisquad.minecraft.csgrenades.item

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

open class CounterStrikeGrenadeItem(properties: Properties) : Item(properties) {

    // Prevent block breaking
    override fun onBlockStartBreak(itemstack: ItemStack?, pos: BlockPos?, player: Player?): Boolean {
        return true
    }
}