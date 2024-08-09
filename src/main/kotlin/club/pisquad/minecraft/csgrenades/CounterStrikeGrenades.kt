package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.registery.ModCreativeTabs
import club.pisquad.minecraft.csgrenades.registery.ModEntities
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import club.pisquad.minecraft.csgrenades.render.FlashBangEffect
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

        ModEntities.ENTITIES.register(KotlinModLoadingContext.get().getKEventBus())
        ModItems.ITEMS.register(KotlinModLoadingContext.get().getKEventBus())
        ModSoundEvents.register(KotlinModLoadingContext.get().getKEventBus())


        CsGrenadePacketHandler.registerMessage()
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        Logger.log(Level.INFO, "Initializing client...")
        KotlinModLoadingContext.get().getKEventBus().addListener(FlashBangEffect::tick)
        KotlinModLoadingContext.get().getKEventBus().addListener(ModCreativeTabs::onCreativeTabBuildContents)

    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        Logger.log(Level.INFO, "Server starting...")
    }
}