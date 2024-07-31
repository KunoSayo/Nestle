package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModData {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Nestle.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NestleData>> NESTLE_DATA = ATTACHMENT_TYPES.register("nestle_data", () -> NestleData.ATTACHMENT_TYPE);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<?>> NESTLE_LEAD_DATA = ATTACHMENT_TYPES.register("nestle_lead_data", () -> NestleLeadData.ATTACHMENT_TYPE);


}
