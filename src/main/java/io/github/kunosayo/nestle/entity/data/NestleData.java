package io.github.kunosayo.nestle.entity.data;

import io.github.kunosayo.nestle.data.NestleValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.UUID;

public class NestleData implements INBTSerializable<CompoundTag> {
    public static final AttachmentType<NestleData> ATTACHMENT_TYPE = AttachmentType.serializable(NestleData::new).copyOnDeath().build();

    public final HashMap<UUID, NestleValue> values = new HashMap<>();

    public NestleData() {
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        values.forEach((uuid, nestleValue) -> tag.put(uuid.toString(), nestleValue.serializeNBT(provider)));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        values.clear();
        var keys = nbt.getAllKeys();
        for (String key : keys) {
            var tag = nbt.getCompound(key);
            values.put(UUID.fromString(key), new NestleValue().chainedDeserializeNBT(provider, tag));
        }
    }

    public NestleValue getValue(UUID uuid) {
        return values.computeIfAbsent(uuid, _u -> new NestleValue());
    }

    public void addValue(UUID uuid, int delta) {
        getValue(uuid).addValue(delta);
    }
}
