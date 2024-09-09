package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.entity.NestleLeadNormalEntity;
import io.github.kunosayo.nestle.entity.NestleLeadPlayerEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Nestle.MOD_ID);

    public static DeferredHolder<EntityType<?>, EntityType<?>> NESTLE_LEAD_PLAYER_ENTITY = ENTITIES.register("nestle_lead_player_entity", () -> NestleLeadPlayerEntity.ENTITY_TYPE);
    public static DeferredHolder<EntityType<?>, EntityType<?>> NESTLE_LEAD_ENTITY = ENTITIES.register("nestle_lead_entity", () -> NestleLeadNormalEntity.ENTITY_TYPE);
}
