package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.item.FlashBangItem
import club.pisquad.minecraft.csgrenades.item.HEGrenadeItem
import club.pisquad.minecraft.csgrenades.item.SmokeGrenadeItem
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, CounterStrikeGrenades.ID)

    val FLASH_BANG_ITEM: RegistryObject<Item> = ITEMS.register("flashbang") { FlashBangItem(Item.Properties()) }

    val SMOKE_GRENADE_ITEM: RegistryObject<Item> =
        ITEMS.register("smokegrenade") { SmokeGrenadeItem(Item.Properties()) }

    val HEGRENADE_ITEM: RegistryObject<Item> = ITEMS.register("hegrenade") { HEGrenadeItem(Item.Properties()) }
}