package club.pisquad.minecraft.csgrenades.entity.client.render

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.entity.FlashBangEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class FlashBangRenderer(context: EntityRendererProvider.Context) : EntityRenderer<FlashBangEntity>(context) {


    companion object {
        val TEXTURE = ResourceLocation(CounterStrikeGrenades.ID, "textures/entity/flashbang.png")
        val MODEL = ResourceLocation(CounterStrikeGrenades.ID, "models/entity/flashbang.json")
    }

    override fun getTextureLocation(p0: FlashBangEntity): ResourceLocation {
        return TEXTURE
    }

    override fun render(
        entity: FlashBangEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, 0.5, 0.0)


        return super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight)
    }

}