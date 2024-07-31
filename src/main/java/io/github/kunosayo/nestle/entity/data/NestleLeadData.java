package io.github.kunosayo.nestle.entity.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

public class NestleLeadData implements INBTSerializable<CompoundTag> {
    public static final AttachmentType<NestleLeadData> ATTACHMENT_TYPE = AttachmentType.serializable(NestleLeadData::new)
            .copyOnDeath()
            .build();

    public UUID target;

    public NestleLeadData() {
    }


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        if (target != null) {
            tag.putUUID("target", target);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        target = null;
        if (nbt.contains("target")) {
            target = nbt.getUUID("target");
        }
    }

}
