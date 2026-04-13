package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeEntity
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class MolotovEntity(pEntityType: EntityType<out MolotovEntity>, pLevel: Level) : FireGrenadeEntity(
    pEntityType, pLevel, MolotovConfig.common.fuseTime.get().toTick().toInt(),
) {
    override val sounds = MolotovRegistries.sounds
    override val damageTypes = MolotovRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.MOLOTOV
}