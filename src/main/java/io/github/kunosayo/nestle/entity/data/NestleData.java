package io.github.kunosayo.nestle.entity.data;

import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.network.SyncNestleValuePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NestleData implements ValueIOSerializable {
    public static final AttachmentType<NestleData> ATTACHMENT_TYPE = AttachmentType.serializable(() -> new NestleData())
            .copyOnDeath()
            .build();

    public static final StreamCodec<ByteBuf, NestleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, NestleValue.STREAM_CODEC),
            nestleData -> nestleData.values,
            NestleData::new
    );

    public final HashMap<UUID, NestleValue> values;

    public NestleData() {
        values = new HashMap<>();
    }

    public NestleData(Map<UUID, NestleValue> values) {
        this.values = new HashMap<>(values);
    }

    public static void addValue(Player who, Player target, int delta) {
        var data = who.getData(ATTACHMENT_TYPE);
        long result = data.getValue(target.getUUID()).addValue(delta);

        if (who instanceof ServerPlayer sp) {
            PacketDistributor.sendToPlayer(sp, new SyncNestleValuePacket(target.getUUID(), result));
        }

    }

    /**
     * The codec to save player values in bytes.
     */
    private static final StreamCodec<ByteBuf, NestleData> SAVE_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, NestleValue.STREAM_CODEC),
            nestleData -> nestleData.values,
            NestleData::new
    );

    public NestleValue getValue(UUID uuid) {
        return values.computeIfAbsent(uuid, _u -> new NestleValue());
    }

    public NestleValue addValue(UUID uuid, int delta, int idx) {
        return getValue(uuid).addValue(delta, idx);
    }

    public NestleValue addDifValue(UUID uuid, int delta) {
        return getValue(uuid).addDifValue(delta);
    }

    public static NestleValue getValueTo(ServerPlayer from, ServerPlayer to) {
        return getValueTo(from, to.getUUID());
    }
    public static NestleValue getValueTo(ServerPlayer from, UUID to) {
        var data = from.getData(ATTACHMENT_TYPE);
        return data.getValue(to);
    }

    @Override
    public void serialize(ValueOutput output) {
        var root = new CompoundTag();

        var buffer = Unpooled.buffer();
        SAVE_CODEC.encode(buffer, this);
        var data = new byte[buffer.writerIndex()];
        buffer.readBytes(data);
        root.putByteArray("data", data);

        output.store(root);
    }

    @Override
    public void deserialize(ValueInput input) {
        input.read("data", ExtraCodecs.NBT)
                .flatMap(Tag::asByteArray).ifPresent(data -> {
                    var nestlePartData = SAVE_CODEC.decode(Unpooled.wrappedBuffer(data));
                    this.values.clear();
                    this.values.putAll(nestlePartData.values);
                });

    }
}
