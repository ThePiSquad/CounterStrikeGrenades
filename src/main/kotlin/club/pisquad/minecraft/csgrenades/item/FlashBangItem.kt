package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents


class FlashBangItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    private var drawSound = ModSoundEvents.FLASHBANG_DRAW.get()
    //    private val logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID + ":flashbang_item")

//    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
//
//        // Server side logic
//        if (!level.isClientSide) {
//            return super.use(level, player, usedHand)
//        }
//
//        val speed = player.deltaMovement.scale(WEAK_THROW_PLAYER_SPEED_SCALE)
//            .add(player.lookAngle.normalize().scale(GrenadeThrowType.Weak.speed))
//            .length()
//
//        val playerPosition = player.position()
//
//        CsGrenadePacketHandler.INSTANCE.sendToServer(
//            GrenadeThrownMessage(
//                player.uuid,
//                speed,
//                GrenadeType.FLASH_BANG,
//                GrenadeThrowType.Weak,
//                Vec3(playerPosition.x, playerPosition.y + PLAYER_EYESIGHT_OFFSET, playerPosition.z),
//                Rotations(player.xRot, player.yRot, 0.0f),
//            )
//        )
//
//        return super.use(level, player, usedHand)
//    }
//
//    override fun onEntitySwing(stack: ItemStack?, player: LivingEntity?): Boolean {
//        if (player == null || !player.level().isClientSide) {
//            return false
//        }
//        val speed =
//            player.deltaMovement.scale(STRONG_THROW_PLAYER_SPEED_SCALE)
//                .add(player.lookAngle.normalize().scale(GrenadeThrowType.Strong.speed))
//                .length()
//        val playerPosition = player.position()
//
//        CsGrenadePacketHandler.INSTANCE.sendToServer(
//            GrenadeThrownMessage(
//                player.uuid,
//                speed,
//                GrenadeType.FLASH_BANG,
//                GrenadeThrowType.Strong,
//                Vec3(playerPosition.x, playerPosition.y + PLAYER_EYESIGHT_OFFSET, playerPosition.z),
//                Rotations(player.xRot, player.yRot, 0.0f),
//            )
//        )
//        return false
//    }
}
