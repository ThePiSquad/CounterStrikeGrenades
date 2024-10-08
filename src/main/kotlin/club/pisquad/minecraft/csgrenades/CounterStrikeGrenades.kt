package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.helper.TickHelper
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.registery.*
import net.minecraftforge.common.MinecraftForge
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
        ModParticles.PARTICLE_TYPES.register(KotlinModLoadingContext.get().getKEventBus())

        MinecraftForge.EVENT_BUS.addListener(TickHelper::tickHandler)
        CsGrenadePacketHandler.registerMessage()
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        Logger.log(Level.INFO, "Initializing client...")

        val eventBus = KotlinModLoadingContext.get().getKEventBus()

        eventBus.addListener(ModCreativeTabs::onCreativeTabBuildContents)
        eventBus.addListener(ModParticleFactories::onRegisterParticleFactories)
//        eventBus.addListener(PlayerInteractEventHandler::onPlayerInteract)
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        Logger.log(Level.INFO, "Server starting...")
    }
}