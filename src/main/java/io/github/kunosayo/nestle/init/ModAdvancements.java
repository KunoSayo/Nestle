package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.advancements.NestleValueRequireCriterionTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModAdvancements {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Nestle.MOD_ID);

    public static final Supplier<NestleValueRequireCriterionTrigger> NESTLE_TRIGGER =
            TRIGGER_TYPES.register("nestle_value_require", NestleValueRequireCriterionTrigger::new);
}
