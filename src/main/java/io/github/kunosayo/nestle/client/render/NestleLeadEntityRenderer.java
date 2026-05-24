package io.github.kunosayo.nestle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kunosayo.nestle.entity.NestleLeadEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class NestleLeadEntityRenderer extends EntityRenderer<NestleLeadEntity, EntityRenderState> {
    public NestleLeadEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    private static Vec3 getLeashLocation(Entity entity, float pTick) {
        if (entity instanceof Leashable leashable) {
            var offset = leashable.getLeashOffset(pTick);
            float entityYRotx = entity.getPreciseBodyRotation(pTick) * (float) (Math.PI / 180.0);
            Vec3 rotatedAttachOffset = offset.yRot(-entityYRotx);
            return rotatedAttachOffset.add(entity.oldPosition().lerp(entity.position(), pTick));
        } else {
            return entity.getRopeHoldPosition(pTick);
        }
    }

    @Override
    public void extractRenderState(NestleLeadEntity pEntity, EntityRenderState state, float partialTicks) {
        state.entityType = pEntity.getType();
        var fromPlayer = pEntity.getSrc();
        var targetPlayer = pEntity.getDst();
        if (fromPlayer == null || targetPlayer == null) {
            return;
        }
        var leash = new EntityRenderState.LeashState();
        var pos = getLeashLocation(fromPlayer, partialTicks);
        // holder (from player) rope location is stored in ERS XYZ
        leash.start = fromPlayer.oldPosition().lerp(fromPlayer.position(), partialTicks);
        // target rope location is stored in end + offset
        leash.end = targetPlayer.oldPosition().lerp(targetPlayer.position(), partialTicks);
        state.x = pos.x;
        state.y = pos.y;
        state.z = pos.z;

        leash.offset = getLeashLocation(targetPlayer, partialTicks).subtract(leash.end);
        state.leashStates = List.of(leash);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.leashStates != null && state.leashStates.size() == 1) {
            // we use state x y z to indicate the holder repo location
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.leash(), new NestleLeadCustomGeometryRenderer(state.leashStates.get(0), new Vec3(state.x, state.y, state.z)));
        }
    }


}
