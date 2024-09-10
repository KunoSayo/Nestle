package io.github.kunosayo.nestle.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.kunosayo.nestle.data.NestleValue;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class NestleValueRequireCriterionTrigger extends SimpleCriterionTrigger<NestleValueRequireTriggerInstance> {

    public static final Codec<NestleValueRequireTriggerInstance> CODEC = RecordCodecBuilder
            .create(instance ->
                    instance.group(
                                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(NestleValueRequireTriggerInstance::player),
                                    Codec.either(
                                            Codec.pair(Codec.INT.fieldOf("distance").codec(),
                                                    Codec.INT.fieldOf("seconds").codec()),
                                            Codec.LONG.fieldOf("require").codec()
                                    ).fieldOf("require").forGetter(NestleValueRequireTriggerInstance::distanceOrValueRequire)
                            )
                            .apply(instance, NestleValueRequireTriggerInstance::new)
            );

    @Override
    public Codec<NestleValueRequireTriggerInstance> codec() {
        return CODEC;
    }


    public void trigger(ServerPlayer player, NestleValue value) {
        this.trigger(player,
                // The condition checker method within the SimpleCriterionTrigger.SimpleInstance subclass
                triggerInstance -> triggerInstance.matches(value)
        );
    }
}