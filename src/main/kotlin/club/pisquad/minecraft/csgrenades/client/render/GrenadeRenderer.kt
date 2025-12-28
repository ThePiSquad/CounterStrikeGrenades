package club.pisquad.minecraft.csgrenades.client.render

import club.pisquad.minecraft.csgrenades.entity.AbstractFireGrenade
import club.pisquad.minecraft.csgrenades.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.GrenadeEntityInterface
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemDisplayContext

class GrenadeRenderer<T>(
    context: EntityRendererProvider.Context
) : EntityRenderer<T>(context) where T : Entity, T : GrenadeEntityInterface {

    private val itemRenderer: ItemRenderer = context.itemRenderer

    override fun render(
        entity: T,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        // Hide fire grenade model after it explodes
        if (entity is AbstractFireGrenade && entity.entityData.get(CounterStrikeGrenadeEntity.isExplodedAccessor)) {
            return
        }

        poseStack.pushPose()

        val itemStack = entity.getItem()
        if (!itemStack.isEmpty && entity is CounterStrikeGrenadeEntity) {
            // 使用 lerp (线性插值) 来平滑地获取实体在两帧之间的旋转角度
            val visualYRot = Mth.lerp(partialTicks, entity.yRotO, entity.yRot)
            val visualXRot = Mth.lerp(partialTicks, entity.xRotO, entity.xRot)
            val visualZRot = Mth.lerp(partialTicks, entity.zRotO, entity.zRot)


            // Translate the model to align its visual center with its physical center
            poseStack.translate(0.0, 0.125, 0.0)

            // 关键：应用实体自身的三轴旋转
            poseStack.mulPose(Axis.YP.rotationDegrees(visualYRot))
            poseStack.mulPose(Axis.ZP.rotationDegrees(visualZRot)) // 使用Z轴
            poseStack.mulPose(Axis.XP.rotationDegrees(visualXRot))

            // 修正模型大小
            poseStack.scale(0.5f, 0.5f, 0.5f)

            // --- START MODIFICATION ---
            val itemName = itemStack.item.descriptionId.replaceFirst("item.csgrenades.", "")
            // Model path for items is usually "models/item/[item_name].json"
            // So for a thrown item, it would be "models/item/[item_name]_t.json"
            val thrownModelLocation = ResourceLocation("csgrenades", "item/${itemName}_t")

            val modelManager = Minecraft.getInstance().itemRenderer.itemModelShaper.modelManager
            val bakedModel: BakedModel? = modelManager.getModel(thrownModelLocation)

            if (bakedModel != null && bakedModel != modelManager.missingModel && !bakedModel.isCustomRenderer) {
                itemRenderer.render(
                    itemStack,
                    ItemDisplayContext.FIXED,
                    false, // left-handed. Doesn't matter much for grenades unless there's specific rendering logic based on this
                    poseStack,
                    buffer,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    bakedModel
                )
            } else {
                // Fallback to original rendering if custom model not found or is a custom renderer
                itemRenderer.renderStatic(
                    itemStack,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.level(),
                    entity.id
                )
            }
            // --- END MODIFICATION ---
        }

        poseStack.popPose()
        // super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight) // 移除super调用以消除阴影和“扭头”效果

    }

    // 这个方法必须重写，对于物品模型渲染，通常返回这个默认值
    override fun getTextureLocation(entity: T): ResourceLocation {
        return TextureAtlas.LOCATION_BLOCKS
    }
}
