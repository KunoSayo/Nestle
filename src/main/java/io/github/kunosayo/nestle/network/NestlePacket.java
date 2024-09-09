package io.github.kunosayo.nestle.network;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.util.NestleUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;


public class NestlePacket implements CustomPacketPayload {
    public static final Type<NestlePacket> NETWORK_TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "nestle"));


    public static final StreamCodec<ByteBuf, NestlePacket> STREAM_CODEC = StreamCodec
            .composite(UUIDUtil.STREAM_CODEC, nestlePacket -> nestlePacket.dst, NestlePacket::new);


    UUID dst;

    public NestlePacket(UUID dst) {
        this.dst = dst;
    }

    public static void serverHandler(final NestlePacket updatePacket, final IPayloadContext context) {
        if (context.protocol().isPlay()) {
            var src = context.player();
            if (src.isDeadOrDying() || src.isRemoved() || src.isSpectator()) {
                return;
            }
            var dst = src.level().getPlayerByUUID(updatePacket.dst);
            if (dst == null || dst.isSpectator() || dst.isRemoved()) {
                return;
            }
            var suid = src.getUUID();
            var duid = dst.getUUID();
            if (src.hasLineOfSight(dst)
                    && src.getData(NestleData.ATTACHMENT_TYPE).getValue(duid).getValue()
                    >= NestleConfig.NESTLE_CONFIG.getLeft().nestleFreeRequire.get()
            ) {

                context.enqueueWork(() -> NestleUtil.playerNestlePlayer(suid, duid, 10));
            }

        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NETWORK_TYPE;
    }

}
