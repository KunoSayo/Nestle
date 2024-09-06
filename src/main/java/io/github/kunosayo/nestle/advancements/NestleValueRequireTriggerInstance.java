package io.github.kunosayo.nestle.advancements;

import io.github.kunosayo.nestle.init.ModAdvancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

import java.util.Optional;

public record NestleValueRequireTriggerInstance(Optional<ContextAwarePredicate> player,
                                                long require) implements SimpleCriterionTrigger.SimpleInstance {

    public static Criterion<NestleValueRequireTriggerInstance> instance(ContextAwarePredicate player, long require) {
        return ModAdvancements.NESTLE_TRIGGER.get().createCriterion(new NestleValueRequireTriggerInstance(Optional.of(player), require));
    }

    public boolean matches(long value) {
        return value >= this.require;
    }
}