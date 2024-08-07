package club.pisquad.minecraft.csgrenades.entity.client.model

import club.pisquad.minecraft.csgrenades.entity.FlashBangEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.RenderType

class FlashBangModel : EntityModel<FlashBangEntity>(RenderType::entityCutoutNoCull) {

    override fun renderToBuffer(
        pPoseStack: PoseStack,
        pBuffer: VertexConsumer,
        pPackedLight: Int,
        pPackedOverlay: Int,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        TODO("Not yet implemented")
    }

    override fun setupAnim(
        pEntity: FlashBangEntity,
        pLimbSwing: Float,
        pLimbSwingAmount: Float,
        pAgeInTicks: Float,
        pNetHeadYaw: Float,
        pHeadPitch: Float
    ) {
        TODO("Not yet implemented")
    }

}