package club.pisquad.minecraft.csgrenades.client.render.flashbang

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashbangRingSoundManager