package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateByFuseGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.messages.HEGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class HEGrenadeEntity(pEntityType: EntityType<out HEGrenadeEntity>, pLevel: Level) :
    ActivateByFuseGrenadeEntity(
        pEntityType,
        pLevel,
        HEGrenadeConfig.common.fuseTime.get().toTick().toInt(),
    ) {
    override val sounds = HEGrenadeRegistries.sounds
    override val damageTypes = HEGrenadeRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.HE_GRENADE


    override fun activate() {
        super.activate()
        if (this.level().isClientSide) {
            // EMPTY
        } else {
            ModPacketHandler.sendMessageToPlayer(
                this.level() as ServerLevel,
                this.center,
                HEGrenadeActivatedMessage(this.center)
            )
            this.discard()
        }
    }


//    private fun getDamageBlockingState(entity: LivingEntity): Double {
//        val headDamage = ClipContext(
//            this.position(),
//            entity.eyePosition,
//            ClipContext.Block.COLLIDER,
//            ClipContext.Fluid.ANY,
//            null,
//        ).let {
//            val clipResult = this.level().clip(it)
//            return@let if (clipResult.type == HitResult.Type.MISS) {
//                val distance = this.position().distanceTo(entity.eyePosition)
//                return@let if (distance < 1.5) {
//                    calculateHEGrenadeDamage(distance, 0.0, true)
//                } else {
//                    calculateHEGrenadeDamage(distance, 0.0)
//                }
//            } else {
//                0.0
//            }
//        }
//
//        val bodyDamage = ClipContext(
//            this.position(),
//            entity.position(),
//            ClipContext.Block.COLLIDER,
//            ClipContext.Fluid.ANY,
//            null,
//        ).let {
//            val clipResult = this.level().clip(it)
//            return@let if (clipResult.type == HitResult.Type.MISS) {
//                calculateHEGrenadeDamage(this.position().distanceTo(entity.position()), 0.0)
//            } else {
//                0.0
//            }
//        }
//
//        return max(headDamage, bodyDamage)
//    }

}

//private fun calculateHEGrenadeDamage(
//    distance: Double,
//    armorReduction: Double,
//    headDamageBoost: Boolean = false,
//): Double {
//    val baseDamage = if (headDamageBoost) ModConfig.hegrenade.BASE_DAMAGE.get() * ModConfig.HEGrenade.HEAD_DAMAGE_BOOST.get() else ModConfig.HEGrenade.BASE_DAMAGE.get()
//    val damageRange = ModConfig.hegrenade.explosionRadius.get()
//    return baseDamage.times(1.0.minus(distance.div(damageRange))).times(1.0.minus(armorReduction))
//}
