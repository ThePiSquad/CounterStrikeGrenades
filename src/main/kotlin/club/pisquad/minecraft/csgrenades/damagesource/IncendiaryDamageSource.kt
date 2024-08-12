package club.pisquad.minecraft.csgrenades.damagesource

import net.minecraft.core.Holder
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType

class IncendiaryDamageSource : DamageSource(Holder.direct<DamageType>(DamageType("Incendiary", 0f))) {
}