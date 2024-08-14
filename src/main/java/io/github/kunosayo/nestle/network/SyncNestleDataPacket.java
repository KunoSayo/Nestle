package io.github.kunosayo.nestle.network;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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
                player.setData(NestleData.ATTACHMENT_TYPE, updatePacket.nestleData);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NETWORK_TYPE;
    }

}
