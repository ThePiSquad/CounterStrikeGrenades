package club.pisquad.minecraft.csgrenades.registery

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModSoundEvents {
    val SOUND_EVENTS: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CounterStrikeGrenades.ID)

    val GRENADE_HIT = registerSoundEvents("grenade.hit")
    val GRENADE_THROW = registerSoundEvents("grenade.throw")

    val FLASHBANG_DRAW = registerSoundEvents("flashbang.draw")
    val FLASHBANG_EXPLODE = registerSoundEvents("flashbang.explode")
    val FLASHBANG_EXPLOSION_RING = registerSoundEvents("flashbang.explosion_ring")

    val SMOKE_GRENADE_DRAW = registerSoundEvents("smokegrenade.draw")
    val SMOKE_EMIT = registerSoundEvents("smokegrenade.smoke_emmit")
    val SMOKE_EXPLODE_DISTANT = registerSoundEvents("smokegrenade.smoke_explode_distant")


    fun register(eventBus: IEventBus) {
        SOUND_EVENTS.register(eventBus)
    }

    private fun registerSoundEvents(name: String): RegistryObject<SoundEvent> {
        // Don't know why forge use the name in json file instead of the actual file location for ResourceLocation
        return SOUND_EVENTS.register(name) {
            SoundEvent.createVariableRangeEvent(ResourceLocation(CounterStrikeGrenades.ID, name))
        }
    }
}