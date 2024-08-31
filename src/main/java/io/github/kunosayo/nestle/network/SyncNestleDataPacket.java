package io.github.kunosayo.nestle.network;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class SyncNestleDataPacket implements CustomPacketPayload {
    public static final Type<SyncNestleDataPacket> NETWORK_TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "sync_nestle"));


    public static final StreamCodec<ByteBuf, SyncNestleDataPacket> STREAM_CODEC = StreamCodec
            .composite(NestleData.STREAM_CODEC, syncNestleDataPacket -> syncNestleDataPacket.nestleData, SyncNestleDataPacket::new);


    NestleData nestleData;

    public SyncNestleDataPacket(NestleData nestleData) {
        this.nestleData = nestleData;
    }

    public static void clientHandler(final SyncNestleDataPacket updatePacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                PlayerNestleInfoList.clientNestleData = updatePacket.nestleData;
                PlayerNestleInfoList.syncNew();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NETWORK_TYPE;
    }

}
