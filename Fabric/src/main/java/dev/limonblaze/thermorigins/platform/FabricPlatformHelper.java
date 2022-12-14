package dev.limonblaze.thermorigins.platform;

import com.google.auto.service.AutoService;
import dev.limonblaze.thermorigins.platform.services.IPlatformHelper;
import dev.limonblaze.thermorigins.power.data.IPowerData;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.*;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import java.util.List;

@AutoService(IPlatformHelper.class)
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <P extends Power> PowerFactory<P> registerPowerFactory(ResourceLocation id, IPowerData<P> power) {
        return Registry.register(ApoliRegistries.POWER_FACTORY, id, power.createFabricFactory(id));
    }

    @Override
    public <P extends Power> List<P> getPowers(LivingEntity entity, Class<P> powerClass, PowerFactory<P> powerFactory) {
        return PowerHolderComponent.getPowers(entity, powerClass);
    }

    @Override
    public <P extends Power> boolean hasPower(LivingEntity entity, Class<P> powerClass, PowerFactory<P> powerFactory) {
        return PowerHolderComponent.hasPower(entity, powerClass);
    }
    
    @Override
    public boolean hasPowerType(LivingEntity entity, ResourceLocation power) {
        return new PowerTypeReference<>(power).isActive(entity);
    }
    
    public void changeResource(LivingEntity entity, ResourceLocation resource, int change) {
        PowerTypeReference<?> powerType = new PowerTypeReference<>(resource);
        Power power = PowerHolderComponent.KEY.get(entity).getPower(powerType);
        if(power instanceof VariableIntPower vip) {
            vip.setValue(vip.getValue() + change);
            PowerHolderComponent.syncPower(entity, powerType);
        } else if(power instanceof CooldownPower cp) {
            cp.modify(change);
            PowerHolderComponent.syncPower(entity, powerType);
        }
    }
    
    @Override
    public SerializableDataType<?> getBiEntityConditionDataType() {
        return ApoliDataTypes.BIENTITY_CONDITION;
    }

    @Override
    public SerializableDataType<?> getBiomeConditionDataType() {
        return ApoliDataTypes.BIOME_CONDITION;
    }

    @Override
    public SerializableDataType<?> getBlockConditionDataType() {
        return ApoliDataTypes.BLOCK_CONDITION;
    }

    @Override
    public SerializableDataType<?> getDamageConditionDataType() {
        return ApoliDataTypes.DAMAGE_CONDITION;
    }

    @Override
    public SerializableDataType<?> getEntityConditionDataType() {
        return ApoliDataTypes.ENTITY_CONDITION;
    }

    @Override
    public SerializableDataType<?> getFluidConditionDataType() {
        return ApoliDataTypes.FLUID_CONDITION;
    }

    @Override
    public SerializableDataType<?> getItemConditionDataType() {
        return ApoliDataTypes.ITEM_CONDITION;
    }

    @Override
    public SerializableDataType<?> getBiEntityActionDataType() {
        return ApoliDataTypes.BIENTITY_ACTION;
    }

    @Override
    public SerializableDataType<?> getBlockActionDataType() {
        return ApoliDataTypes.BLOCK_ACTION;
    }

    @Override
    public SerializableDataType<?> getEntityActionDataType() {
        return ApoliDataTypes.ENTITY_ACTION;
    }

    @Override
    public SerializableDataType<?> getItemActionDataType() {
        return ApoliDataTypes.ITEM_ACTION;
    }
    
    @Override
    public int getBurnTimeForFuel(ItemStack stack, RecipeType<?> recipeType) {
        if(stack.isEmpty()) {
            return 0;
        } else {
            Item item = stack.getItem();
            return AbstractFurnaceBlockEntity.getFuel().getOrDefault(item, 0);
        }
    }
    
}
