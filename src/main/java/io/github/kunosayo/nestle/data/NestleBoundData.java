package io.github.kunosayo.nestle.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

/**
 * Data component for storing bound player information on the nestle_bound item.
 */
public record NestleBoundData(UUID id) {

    public static final Codec<NestleBoundData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(NestleBoundData::id)
            ).apply(instance, NestleBoundData::new)
    );

    public static final StreamCodec<ByteBuf, NestleBoundData> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            NestleBoundData::id,
            NestleBoundData::new
    );

    public String playerName() {
        return PlayerNestleInfoList.getPlayerNameById(id);
    }
}
