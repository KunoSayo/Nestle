package io.github.kunosayo.nestle.entity.data;

import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.network.SyncNestleValuePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NestleData implements INBTSerializable<CompoundTag> {
    public static final AttachmentType<NestleData> ATTACHMENT_TYPE = AttachmentType.serializable(() -> new NestleData())
            .copyOnDeath()
            .build();

    public static final StreamCodec<ByteBuf, NestleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, NestleValue.STREAM_CODEC),
            nestleData -> nestleData.values,
            NestleData::new
    );

    public final HashMap<UUID, NestleValue> values;
    public boolean givenStartItem;

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

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        var root = new CompoundTag();
        values.forEach((uuid, nestleValue) -> tag.put(uuid.toString(), nestleValue.serializeNBT(provider)));
        root.putBoolean("givenStartItem", givenStartItem);
        root.put("players", tag);
        return root;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        values.clear();
        var players = nbt.getCompound("players");
        var keys = players.getAllKeys();
        for (String key : keys) {
            var tag = players.getCompound(key);
            values.put(UUID.fromString(key), new NestleValue().chainedDeserializeNBT(provider, tag));
        }
        givenStartItem = nbt.getBoolean("givenStartItem");
    }

    public NestleValue getValue(UUID uuid) {
        return values.computeIfAbsent(uuid, _u -> new NestleValue());
    }

    public NestleValue addValue(UUID uuid, int delta, int idx) {
        return getValue(uuid).addValue(delta, idx);
    }

    public NestleValue addDifValue(UUID uuid, int delta) {
        return getValue(uuid).addDifValue(delta);
    }
}
