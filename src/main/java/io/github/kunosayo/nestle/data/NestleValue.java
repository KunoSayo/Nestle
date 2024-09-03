package io.github.kunosayo.nestle.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class NestleValue implements INBTSerializable<CompoundTag> {
    public static final StreamCodec<ByteBuf, NestleValue> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, nestleValue -> nestleValue.value,
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buffer, int[] value) {
                    for (int i = 0; i < 18; i++) {
                        VarInt.write(buffer, value[i]);
                    }
                }

                @Override
                public int[] decode(ByteBuf buffer) {
                    var arr = new int[18];
                    for (int i = 0; i < 18; i++) {
                        arr[i] = VarInt.read(buffer);
                    }
                    return arr;
                }
            }, nestleValue -> nestleValue.times, NestleValue::new
    );
    // 2^0 ... 2^15,  far away.., different world
    public int[] times = new int[18];
    private long value;

    public NestleValue() {
        value = 0;
    }

    public NestleValue(int value) {
        this.value = value;
    }

    public NestleValue(long value, int[] times) {
        this.value = value;
        this.times = times;
    }

    /**
     * Get the index for times array from the player squared distance
     *
     * @param disSqr the player squared distance
     * @return 0 ~ 15 for 2^i (1 2 4 8 16 ..) and 16 for else.
     */
    public static int getIndex(double disSqr) {
        if (disSqr > 1073741824) {
            return 16;
        }
        int dis = (int) (disSqr + 0.99999);
        int high = 32 - Integer.numberOfLeadingZeros(dis);
        int extra = (dis ^ (1 << (high - 1))) != 0 ? 1 : 0;
        return (high >>> 1) + (extra & high);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("value", value);
        tag.putIntArray("times", times);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        value = nbt.getLong("value");
        times = nbt.getIntArray("times");
        if (times.length != 18) {
            times = new int[18];
        }
    }


    public NestleValue chainedDeserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        deserializeNBT(provider, nbt);
        return this;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public NestleValue addValue(int delta, int idx) {
        this.value = Math.min(Math.max(value + delta, -999), Long.MAX_VALUE >>> 1);
        ++times[idx];
        return this;
    }

    public NestleValue addDifValue(int delta) {
        this.value = Math.min(Math.max(value + delta, -999), Long.MAX_VALUE >>> 1);
        ++times[17];
        return this;
    }

    public long addValue(int delta) {
        return this.value = Math.min(Math.max(value + delta, -999), Long.MAX_VALUE >>> 1);
    }
}
