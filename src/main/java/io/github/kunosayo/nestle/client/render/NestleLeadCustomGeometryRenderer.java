package io.github.kunosayo.nestle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;

public class NestleLeadCustomGeometryRenderer implements SubmitNodeCollector.CustomGeometryRenderer {

    private final EntityRenderState.LeashState leashState;
    private final Vec3 holderRopeLocation;

    public NestleLeadCustomGeometryRenderer(EntityRenderState.LeashState leashState, Vec3 holderRopeLocation) {
        this.leashState = leashState;
        this.holderRopeLocation = holderRopeLocation;
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
        int k = LightCoordsUtil.pack(i, j);
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

    private void renderLeash(PoseStack.Pose pPoseStack, VertexConsumer vertexConsumer) {


        // We use A B for position and U V for repo location.
        // A - B + U - A
        // offset is

        // we need translate
//        pPoseStack.translate(pEntity.getX() - self.getX(), pEntity.getY() - self.getY(), pEntity.getZ() - self.getZ());
//        pPoseStack.translate((float) leashState.offset.x, (float) leashState.offset.y, (float) leashState.offset.z);

//

        // holder (from player) rope location is stored in ERS XYZ
        // target rope location is stored in end + offset

        var targetRopeLocation = leashState.end.add(leashState.offset);
        float dx = (float) -(holderRopeLocation.x - targetRopeLocation.x);
        float dy = (float) -(holderRopeLocation.y - targetRopeLocation.y);
        float dz = (float) -(holderRopeLocation.z - targetRopeLocation.z);
        Matrix4f matrix4f = pPoseStack.pose();
        float f4 = Mth.invSqrt(dx * dx + dz * dz) * 0.025F / 2.0F;
        float f5 = dz * f4;
        float f6 = dx * f4;


//        BlockPos blockpos = BlockPos.containing(pEntity.getEyePosition(pPartialTick));
//        BlockPos blockpos1 = BlockPos.containing(pLeashHolder.getEyePosition(pPartialTick));
//        int i = pEntity.level().getBrightness(LightLayer.BLOCK, blockpos);
//        int j = pLeashHolder.level().getBrightness(LightLayer.BLOCK, blockpos1);
//        int k = pEntity.level().getBrightness(LightLayer.SKY, blockpos);
//        int l = pEntity.level().getBrightness(LightLayer.SKY, blockpos1);
        int i = leashState.startBlockLight;
        int j = leashState.endBlockLight;
        int k = leashState.startSkyLight;
        int l = leashState.endSkyLight;

        for (int i1 = 0; i1 <= 24; i1++) {
            addVertexPair(vertexConsumer, matrix4f, dx, dy, dz, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
        }

        for (int j1 = 24; j1 >= 0; j1--) {
            addVertexPair(vertexConsumer, matrix4f, dx, dy, dz, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
        }

    }

    @Override
    public void render(PoseStack.@NonNull Pose pose, @NonNull VertexConsumer vertexConsumer) {
        renderLeash(pose, vertexConsumer);
    }
}
