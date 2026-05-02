package io.github.kunosayo.nestle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kunosayo.nestle.entity.NestleLeadEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
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


    @Override
    public void extractRenderState(NestleLeadEntity pEntity, EntityRenderState state, float partialTicks) {
        var fromPlayer = pEntity.getSrc();
        var targetPlayer = pEntity.getDst();
        if (fromPlayer == null || targetPlayer == null) {
            return;
        }

        var leash = new EntityRenderState.LeashState();
        var pos = fromPlayer.getRopeHoldPosition(partialTicks);
        state.x = pos.x;
        state.y = pos.y;
        state.z = pos.z;
        leash.start = fromPlayer.oldPosition().lerp(fromPlayer.position(), partialTicks);
        leash.end = targetPlayer.oldPosition().lerp(targetPlayer.position(), partialTicks);
        leash.offset = leash.end.subtract(targetPlayer.getRopeHoldPosition(partialTicks));
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
