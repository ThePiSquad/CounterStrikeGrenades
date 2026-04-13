package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateByFuseGrenadeEntity
import club.pisquad.minecraft.csgrenades.getPlayersWithinMessageRange
import club.pisquad.minecraft.csgrenades.grenades.flashbang.messages.FlashbangActivatedMessage
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import java.util.*

class FlashBangEntity(pEntityType: EntityType<out FlashBangEntity>, pLevel: Level) : ActivateByFuseGrenadeEntity(
    pEntityType, pLevel,
    FlashBangConfig.common.fuseTime.get().toTick().toInt(),
) {
    override val sounds = FlashbangRegistries.sounds
    override val damageTypes = FlashbangRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.FLASH_BANG


    override fun activate() {
        super.activate()
        if (this.level().isClientSide) {
            //EMPTY
        } else {
            val data = generateAffectedPlayerData()
            ModLogger.debug(this) { "Sending FlashbangActivatedMessage with data size:${data.size}" }
            ModPacketHandler.sendMessageToPlayer(
                this.level() as ServerLevel, this.center, FlashbangActivatedMessage(
                    this.center,
                    data
                )
            )
            this.discard()
        }
    }

    private fun generateAffectedPlayerData(): Map<UUID, FlashbangBlindEffectData> {
        val level: ServerLevel = this.level() as ServerLevel
        val players = level.getPlayersWithinMessageRange(this.center)
        val result = mutableMapOf<UUID, FlashbangBlindEffectData>()
        players.forEach {
            val data = FlashbangEffectCalculator.calculate(this.center, it.eyePosition, it.lookAngle)
            if (data != null) {
                result[it.uuid] = data
            }
        }
        return result
    }

}
