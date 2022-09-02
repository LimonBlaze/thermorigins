package com.example.examplemod.mixin;

import com.example.examplemod.platform.Services;
import com.example.examplemod.power.ActionOnEquipPower;
import com.example.examplemod.registry.ExamplePowerFactories;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "setItemSlot", at = @At(value = "TAIL"))
    public void example$equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (slot.getType() != EquipmentSlot.Type.ARMOR && !slot.equals(EquipmentSlot.OFFHAND)) return;

        Services.PLATFORM.getPowers((Player)(Object)this, ActionOnEquipPower.class, ExamplePowerFactories.ACTION_ON_EQUIP).forEach(power -> power.fireAction(slot, stack));
    }
}