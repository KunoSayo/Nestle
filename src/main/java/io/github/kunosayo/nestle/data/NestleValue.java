package io.github.kunosayo.nestle.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class NestleValue implements INBTSerializable<CompoundTag> {
    private long value;

    public NestleValue() {
        value = 0;
    }

    public NestleValue(int value) {
        this.value = value;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("value", value);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        value = nbt.getLong("value");
    }


    public NestleValue chainedDeserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        deserializeNBT(provider, nbt);
        return this;
    }

    public long getValue() {
        return this.value;
    }

    public void addValue(long delta) {
        this.value = Math.max(value + delta, -999);
    }
}
