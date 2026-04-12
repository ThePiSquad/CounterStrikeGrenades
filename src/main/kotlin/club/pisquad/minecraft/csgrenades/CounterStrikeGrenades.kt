package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.RegistryHelper
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext


@Mod(CounterStrikeGrenades.ID)
class CounterStrikeGrenades(context: FMLJavaModLoadingContext) {

    init {
        ModLogger.info("Initializing Mod -- Common Setup")

        RegistryHelper.commonSetup(context.modEventBus)

        ModPacketHandler.register()

        ModLogger.info("Generating/Reading server-side config")
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SPEC)
    }

    companion object {
        const val ID = "csgrenades"

        @JvmStatic
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent) {
            ModLogger.info("Initializing Mod -- Client Setup")
        }

        @JvmStatic
        @SubscribeEvent
        fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
            ModLogger.info("Initializing Mod -- Server Setup")
        }
    }
}
