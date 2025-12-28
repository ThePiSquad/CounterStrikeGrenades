package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.command.ModCommands
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.registery.*
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */

@Mod(CounterStrikeGrenades.ID)
object CounterStrikeGrenades {
    const val ID = "csgrenades"

    // the logger for our mod
    val Logger: Logger = LogManager.getLogger(ID)

    init {

        Logger.log(Level.INFO, "Hello Counter Strike Grenades")

        val bus = KotlinModLoadingContext.get().getKEventBus()

        ModEntities.ENTITIES.register(bus)
        ModItems.ITEMS.register(bus)
        ModSoundEvents.register(bus)
        ModParticles.PARTICLE_TYPES.register(bus)
        ModCreativeTabs.CREATIVE_MODE_TABS.register(bus)
        bus.addListener(::removeFromCreativeTabs)

        CsGrenadePacketHandler.registerMessage()
        MinecraftForge.EVENT_BUS.register(ModCommands)
        ModSerializers.register()
        net.minecraftforge.fml.ModLoadingContext.get()
            .registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SPEC)

    }

    private fun removeFromCreativeTabs(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.COMBAT) {
            val itemsToRemove = setOf(
                ModItems.HEGRENADE_ITEM.get(),
                ModItems.SMOKE_GRENADE_ITEM.get(),
                ModItems.FLASH_BANG_ITEM.get(),
                ModItems.DECOY_GRENADE_ITEM.get(),
                ModItems.MOLOTOV_ITEM.get(),
                ModItems.INCENDIARY_ITEM.get()
            )

            // Correct way: Use an iterator on the entry set to safely remove.
            val iterator = event.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.item in itemsToRemove) {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        Logger.log(Level.INFO, "Initializing client...")

        val eventBus = KotlinModLoadingContext.get().getKEventBus()

        eventBus.addListener(ModParticleFactories::onRegisterParticleFactories)

    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        Logger.log(Level.INFO, "Server starting...")
    }
}