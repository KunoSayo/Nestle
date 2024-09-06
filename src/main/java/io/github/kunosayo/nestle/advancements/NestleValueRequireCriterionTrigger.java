package io.github.kunosayo.nestle.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class NestleValueRequireCriterionTrigger extends SimpleCriterionTrigger<NestleValueRequireTriggerInstance> {

    public static final Codec<NestleValueRequireTriggerInstance> CODEC = RecordCodecBuilder
            .create(instance ->
                    instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(NestleValueRequireTriggerInstance::player),
                                    Codec.LONG.fieldOf("require").forGetter(NestleValueRequireTriggerInstance::require))
                            .apply(instance, NestleValueRequireTriggerInstance::new)
            );

    @Override
    public Codec<NestleValueRequireTriggerInstance> codec() {
        return CODEC;
    }


    public void trigger(ServerPlayer player, long value) {
        this.trigger(player,
                // The condition checker method within the SimpleCriterionTrigger.SimpleInstance subclass
                triggerInstance -> triggerInstance.matches(value)
        );
    }
}