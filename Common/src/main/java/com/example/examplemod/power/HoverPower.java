package com.example.examplemod.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.world.entity.LivingEntity;

public class HoverPower extends Power {
    public HoverPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
}