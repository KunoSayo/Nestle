package io.github.kunosayo.nestle.network;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import io.github.kunosayo.nestle.data.NestleValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;


public class SyncNestleValuePacket implements CustomPacketPayload {
    public static final Type<SyncNestleValuePacket> NETWORK_TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "sync_nestle_value"));


    public static final StreamCodec<ByteBuf, SyncNestleValuePacket> STREAM_CODEC = StreamCodec
            .composite(UUIDUtil.STREAM_CODEC, packet -> packet.target, ByteBufCodecs.VAR_LONG, packet -> packet.value,
                    SyncNestleValuePacket::new);


    UUID target;
    long value;

    public SyncNestleValuePacket(UUID target, long value) {
        this.target = target;
        this.value = value;
    }

    public static void clientValueHandler(final SyncNestleValuePacket updatePacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            PlayerNestleInfoList.clientNestleData.values.computeIfAbsent(updatePacket.target, uuid -> new NestleValue()).setValue(updatePacket.value);
            PlayerNestleInfoList.setDirty();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NETWORK_TYPE;
    }

}
