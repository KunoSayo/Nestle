package io.github.kunosayo.nestle.network;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UpdateNestleValuePacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateNestleValuePacket> NETWORK_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "update_nestle"));


    public static final StreamCodec<ByteBuf, UpdateNestleValuePacket> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.<ByteBuf, DifferentWorldUpdate>list().apply(DifferentWorldUpdate.STREAM_CODEC),
                    UpdateNestleValuePacket::getDifferentWorld,
                    ByteBufCodecs.<ByteBuf, SameWorldUpdate>list().apply(SameWorldUpdate.STREAM_CODEC),
                    UpdateNestleValuePacket::getSameWorld,
                    UpdateNestleValuePacket::new);

    List<DifferentWorldUpdate> differentWorld;
    // added value, added index
    List<SameWorldUpdate> sameWorld;

    public UpdateNestleValuePacket() {
        differentWorld = new ArrayList<>();
        sameWorld = new ArrayList<>();
    }

    public UpdateNestleValuePacket(List<DifferentWorldUpdate> differentWorld, List<SameWorldUpdate> sameWorld) {
        this.differentWorld = differentWorld;
        this.sameWorld = sameWorld;
    }

    public static void clientHandler(final UpdateNestleValuePacket updatePacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                var nestleData = player.getData(NestleData.ATTACHMENT_TYPE);
                for (var dif : updatePacket.differentWorld) {
                    var v = nestleData.addDifValue(dif.target, dif.added);
                    PlayerNestleInfoList.updatePlayer(dif.target, v);
                }
                for (var sameWorld : updatePacket.sameWorld) {
                    var v = nestleData.addValue(sameWorld.target, sameWorld.added, sameWorld.timesIndex);
                    PlayerNestleInfoList.updatePlayer(sameWorld.target, v);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NETWORK_TYPE;
    }

    public List<DifferentWorldUpdate> getDifferentWorld() {
        return differentWorld;
    }

    public List<SameWorldUpdate> getSameWorld() {
        return sameWorld;
    }

    public record SameWorldUpdate(UUID target, int added, int timesIndex) {
        public static final StreamCodec<ByteBuf, SameWorldUpdate> STREAM_CODEC = StreamCodec
                .composite(UUIDUtil.STREAM_CODEC, SameWorldUpdate::target,
                        ByteBufCodecs.VAR_INT, SameWorldUpdate::added,
                        ByteBufCodecs.VAR_INT, SameWorldUpdate::timesIndex,
                        SameWorldUpdate::new);

        @Override
        public UUID target() {
            return target;
        }

        @Override
        public int added() {
            return added;
        }

        @Override
        public int timesIndex() {
            return timesIndex;
        }
    }

    public record DifferentWorldUpdate(UUID target, int added) {
        public static final StreamCodec<ByteBuf, DifferentWorldUpdate> STREAM_CODEC = StreamCodec
                .composite(UUIDUtil.STREAM_CODEC, DifferentWorldUpdate::target,
                        ByteBufCodecs.VAR_INT, DifferentWorldUpdate::added,
                        DifferentWorldUpdate::new);

        @Override
        public UUID target() {
            return target;
        }

        @Override
        public int added() {
            return added;
        }


    }
}
