package io.github.kunosayo.nestle.advancements;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.init.ModAdvancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

import java.util.Optional;

/**
 * @param player
 * @param distanceOrValueRequire Either&lt;Pair&lt;Distance Index, Distance Seconds&gt;, Nestle Value require&gt;
 *                               If the nestle value require is negative, then the value must <= negative, otherwise the value must >= require
 */
public record NestleValueRequireTriggerInstance(Optional<ContextAwarePredicate> player,
                                                Either<Pair<Integer, Integer>, Long> distanceOrValueRequire) implements SimpleCriterionTrigger.SimpleInstance {

    public static Criterion<NestleValueRequireTriggerInstance> instance(ContextAwarePredicate player, Either<Pair<Integer, Integer>, Long> condition) {
        return ModAdvancements.NESTLE_TRIGGER.get().createCriterion(new NestleValueRequireTriggerInstance(Optional.of(player), condition));
    }

    public static Criterion<NestleValueRequireTriggerInstance> instance(Either<Pair<Integer, Integer>, Long> condition) {
        return ModAdvancements.NESTLE_TRIGGER.get().createCriterion(new NestleValueRequireTriggerInstance(Optional.empty(), condition));
    }

    public boolean matches(NestleValue value) {
        return this.distanceOrValueRequire.map(distanceRequire -> {
            if (distanceRequire.getFirst() >= value.times.length || distanceRequire.getFirst() < 0) {
                return false;
            }
            return distanceRequire.getSecond() <= value.times[distanceRequire.getFirst()];
        }, require -> {
            if (require == 0) {
                return value.getValue() >= NestleConfig.NESTLE_CONFIG.getLeft().nestleFreeRequire.get();
            }
            if (require < 0) {
                return value.getValue() <= require;
            }
            return value.getValue() >= require;
        });
    }
}