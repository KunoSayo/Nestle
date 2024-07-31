package io.github.kunosayo.nestle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kunosayo.nestle.entity.NestleLeadEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class NestleLeadEntityRenderer extends EntityRenderer<NestleLeadEntity> {
    public NestleLeadEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    private static void addVertexPair(
            VertexConsumer pBuffer,
            Matrix4f pPose,
            float pStartX,
            float pStartY,
            float pStartZ,
            int pEntityBlockLight,
            int pHolderBlockLight,
            int pEntitySkyLight,
            int pHolderSkyLight,
            float pYOffset,
            float pDy,
            float pDx,
            float pDz,
            int pIndex,
            boolean pReverse
    ) {
        float f = (float) pIndex / 24.0F;
        int i = (int) Mth.lerp(f, (float) pEntityBlockLight, (float) pHolderBlockLight);
        int j = (int) Mth.lerp(f, (float) pEntitySkyLight, (float) pHolderSkyLight);
        int k = LightTexture.pack(i, j);
        float f1 = pIndex % 2 == (pReverse ? 1 : 0) ? 0.7F : 1.0F;
        int rgb = 0xd95763;
        float r = ((rgb >> 16) / 255.0f) * f1;
        float g = (((rgb >> 8) & 0xff) / 255.0f) * f1;
        float b = (((rgb) & 0xff) / 255.0f) * f1;
        float f5 = pStartX * f;
        float f6 = pStartY > 0.0F ? pStartY * f * f : pStartY - pStartY * (1.0F - f) * (1.0F - f);
        float f7 = pStartZ * f;
        pBuffer.addVertex(pPose, f5 - pDx, f6 + pDy, f7 + pDz).setColor(r, g, b, 1.0F).setLight(k);
        pBuffer.addVertex(pPose, f5 + pDx, f6 + pYOffset - pDy, f7 - pDz).setColor(r, g, b, 1.0F).setLight(k);
    }

    @Override
    public ResourceLocation getTextureLocation(NestleLeadEntity pEntity) {
        return null;
    }

    @Override
    public void render(NestleLeadEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {
        var level = pEntity.level();
        var fromPlayer = level.getPlayerByUUID(pEntity.from);
        var targetPlayer = level.getPlayerByUUID(pEntity.target);
        if (fromPlayer == null || targetPlayer == null) {
            return;
        }
        renderLeash(pEntity, fromPlayer, pPartialTick, pPoseStack, pBufferSource, targetPlayer);
    }

    private void renderLeash(Entity self, Player pEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, Player pLeashHolder) {
        pPoseStack.pushPose();


//        pPoseStack.translate(pEntity.getX() - self.getX(), pEntity.getY() - self.getY(), pEntity.getZ() - self.getZ());
        pPoseStack.translate(pEntity.getX() - Mth.lerp(pPartialTick, self.xo, self.getX()),
                pEntity.getY() - Mth.lerp(pPartialTick, self.yo, self.getY()),
                pEntity.getZ() - Mth.lerp(pPartialTick, self.zo, self.getZ()));

        Vec3 endPoint = pLeashHolder.getRopeHoldPosition(pPartialTick);
        Vec3 leashedOffset = pEntity.getRopeHoldPosition(pPartialTick).subtract(pEntity.position());


        double lineStartX = leashedOffset.x + pEntity.getX();
        double lineStartY = leashedOffset.y + pEntity.getY();
        double lineStartZ = leashedOffset.z + pEntity.getZ();
        // translate to the start point.
        pPoseStack.translate(leashedOffset.x, leashedOffset.y, leashedOffset.z);

        float startX = (float) (endPoint.x - lineStartX);
        float startY = (float) (endPoint.y - lineStartY);
        float startZ = (float) (endPoint.z - lineStartZ);
        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix4f = pPoseStack.last().pose();
        float f4 = Mth.invSqrt(startX * startX + startZ * startZ) * 0.025F / 2.0F;
        float f5 = startZ * f4;
        float f6 = startX * f4;
        BlockPos blockpos = BlockPos.containing(pEntity.getEyePosition(pPartialTick));
        BlockPos blockpos1 = BlockPos.containing(pLeashHolder.getEyePosition(pPartialTick));
        int i = pEntity.level().getBrightness(LightLayer.BLOCK, blockpos);
        int j = pLeashHolder.level().getBrightness(LightLayer.BLOCK, blockpos1);
        int k = pEntity.level().getBrightness(LightLayer.SKY, blockpos);
        int l = pEntity.level().getBrightness(LightLayer.SKY, blockpos1);

        for (int i1 = 0; i1 <= 24; i1++) {
            addVertexPair(vertexconsumer, matrix4f, startX, startY, startZ, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
        }

        for (int j1 = 24; j1 >= 0; j1--) {
            addVertexPair(vertexconsumer, matrix4f, startX, startY, startZ, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
        }

        pPoseStack.popPose();
    }
}
