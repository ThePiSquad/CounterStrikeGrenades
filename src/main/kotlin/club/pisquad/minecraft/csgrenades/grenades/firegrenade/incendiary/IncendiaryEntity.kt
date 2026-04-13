package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeEntity
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class IncendiaryEntity(pEntityType: EntityType<out IncendiaryEntity>, pLevel: Level) : FireGrenadeEntity(
    pEntityType, pLevel,
    IncendiaryConfig.common.fuseTime.get().toTick().toInt(),
) {
    override val sounds = IncendiaryRegistries.sounds
    override val damageTypes = IncendiaryRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.INCENDIARY
}