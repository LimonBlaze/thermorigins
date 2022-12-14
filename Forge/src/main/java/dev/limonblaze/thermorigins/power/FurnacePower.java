package dev.limonblaze.thermorigins.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.limonblaze.thermorigins.data.FurnaceFactory;
import dev.limonblaze.thermorigins.data.ThermoDataTypes;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.IInventoryPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

/**
 * Power that grants player a workable furnace. <br>
 * {@link Configuration#furnace}: A furnace instance, can have type of furnace, blast furnace and smoker. <br>
 * {@link Configuration#shouldDropOnDeath}: If the contents in the furnace should drop on death. <br>
 * {@link Configuration#dropOnDeathFilter}: Item Condition to check if a certain stack should drop on death. <br>
 * {@link Configuration#key}: Key to open the furnace menu. <br>
 * {@link Configuration#openEntityCondition}: Entity Condition to check if the player can open the furnace menu. <br>
 * {@link Configuration#activeEntityAction}: Entity Action which performs every tick when the furnace is "running". <br>
 * {@link Configuration#idleEntityAction}: Entity Action which performs every tick when the furnace is not "running". <br>
 * {@link Configuration#smeltedEntityAction}: Entity Action which performs when the furnace smelted an item. <br>
 * {@link Configuration#smeltedInputCondition}: Item Condition to check on the input item if the furnace should perform actions on smelted. <br>
 * {@link Configuration#smeltedOutputCondition}: Item Condition to check on the output item if the furnace should perform actions on smelted. <br>
 * {@link Configuration#smeltedOutputAction}: Item Action which performs on the output item, mostly for modify usage. <b>Don't use random-based item action, this action will be called for simulation, and mismatched results may encounter unexpected behaviors.</b> <br>
 * @see FurnaceFactory
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FurnacePower extends PowerFactory<FurnacePower.Configuration> implements
    IActivePower<FurnacePower.Configuration>,
    IInventoryPower<FurnacePower.Configuration>
{
    public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ThermoDataTypes.FURNACE.fieldOf("furnace_type")
            .forGetter(Configuration::furnace),
        CalioCodecHelper.optionalField(Codec.STRING, "title")
            .forGetter(x -> Optional.ofNullable(x.title)),
        CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "drop_on_death", true)
            .forGetter(Configuration::shouldDropOnDeath),
        ConfiguredItemCondition.optional("drop_on_death_filter")
            .forGetter(Configuration::dropOnDeathFilter),
        ConfiguredEntityCondition.optional("open_entity_condition")
            .forGetter(Configuration::openEntityCondition),
        ConfiguredEntityAction.optional("active_entity_action")
            .forGetter(Configuration::activeEntityAction),
        ConfiguredEntityAction.optional("idle_entity_action")
            .forGetter(Configuration::idleEntityAction),
        ConfiguredEntityAction.optional("smelted_entity_action")
            .forGetter(Configuration::smeltedEntityAction),
        ConfiguredItemCondition.optional("smelted_input_condition")
            .forGetter(Configuration::smeltedInputCondition),
        ConfiguredItemCondition.optional("smelted_output_condition")
            .forGetter(Configuration::smeltedOutputCondition),
        ConfiguredItemAction.optional("smelted_output_action")
            .forGetter(Configuration::smeltedOutputAction),
        CalioCodecHelper.optionalField(IActivePower.Key.BACKWARD_COMPATIBLE_CODEC, "key", IActivePower.Key.PRIMARY)
            .forGetter(Configuration::key)
    ).apply(instance, (furnace, title, drop, filter,
                       openEntityCondition, activeEntityAction, idleEntityAction,
                       smeltedEntityAction, smeltedInputCondition, smeltedOutputCondition,
                       smeltedOutputAction, key) ->
        new Configuration(furnace, title.orElse(null), drop, filter,
            openEntityCondition, activeEntityAction, idleEntityAction,
            smeltedEntityAction, smeltedInputCondition, smeltedOutputCondition,
            smeltedOutputAction, key)
    ));
    
    public FurnacePower() {
        super(CODEC);
        this.ticking();
    }
    
    @Override
    public void activate(ConfiguredPower<Configuration, ?> power, Entity entity) {
        if(!entity.level.isClientSide() && entity instanceof Player player) {
            if(power.isActive(entity) && ConfiguredEntityCondition.check(power.getConfiguration().openEntityCondition, player)) {
                player.openMenu(this.getInventory(power, entity));
            }
        }
    }
    
    @Override
    public Key getKey(ConfiguredPower<Configuration, ?> power, @Nullable Entity entity) {
        return power.getConfiguration().key;
    }
    
    @Override
    public boolean shouldDropOnDeath(ConfiguredPower<Configuration, ?> power, Entity entity, ItemStack itemStack) {
        return ConfiguredItemCondition.check(power.getConfiguration().dropOnDeathFilter, entity.level, itemStack);
    }
    
    @Override
    public boolean shouldDropOnDeath(ConfiguredPower<Configuration, ?> power, Entity entity) {
        return power.getConfiguration().shouldDropOnDeath;
    }
    
    @Override
    public FurnaceFactory.Instance getInventory(ConfiguredPower<Configuration, ?> power, Entity entity) {
        Configuration config = power.getConfiguration();
        return power.getPowerData(entity, () -> config.furnace.createInstance(config.title, config::onItemSmelted));
    }
    
    public FurnaceFactory.Instance getInventory(ConfiguredPower<Configuration, ?> power, IPowerContainer container) {
        Configuration config = power.getConfiguration();
        return power.getPowerData(container, () -> config.furnace.createInstance(config.title, config::onItemSmelted));
    }
    
    @Override
    public MenuConstructor getMenuCreator(ConfiguredPower<Configuration, ?> power, Entity entity) {
        return getInventory(power, entity);
    }
    
    @Override
    public void deserialize(ConfiguredPower<Configuration, ?> power, IPowerContainer container, CompoundTag tag) {
        getInventory(power, container).fromTag(tag);
    }
    
    @Override
    public void serialize(ConfiguredPower<Configuration, ?> power, IPowerContainer container, CompoundTag tag) {
        getInventory(power, container).toTag(tag);
    }
    
    @Override
    public void tick(ConfiguredPower<Configuration, ?> power, Entity entity) {
        if(!entity.level.isClientSide && entity.isAlive() && power.isActive(entity)) {
            Configuration config = power.getConfiguration();
            FurnaceFactory.Instance furnace = getInventory(power, entity);
            if(furnace.tick((LivingEntity) entity)) {
                ConfiguredEntityAction.execute(config.activeEntityAction, entity);
            } else {
                ConfiguredEntityAction.execute(config.idleEntityAction, entity);
            }
            if(furnace.getChanged()) ApoliAPI.synchronizePowerContainer(entity);
        }
    }
    
    public record Configuration(FurnaceFactory furnace,
                                @Nullable String title,
                                boolean shouldDropOnDeath,
                                Holder<ConfiguredItemCondition<?, ?>> dropOnDeathFilter,
                                Holder<ConfiguredEntityCondition<?, ?>> openEntityCondition,
                                Holder<ConfiguredEntityAction<?, ?>> activeEntityAction,
                                Holder<ConfiguredEntityAction<?, ?>> idleEntityAction,
                                Holder<ConfiguredEntityAction<?, ?>> smeltedEntityAction,
                                Holder<ConfiguredItemCondition<?, ?>> smeltedInputCondition,
                                Holder<ConfiguredItemCondition<?, ?>> smeltedOutputCondition,
                                Holder<ConfiguredItemAction<?, ?>> smeltedOutputAction,
                                IActivePower.Key key
    ) implements IDynamicFeatureConfiguration {
    
        public ItemStack onItemSmelted(ItemStack input, ItemStack result, LivingEntity entity, boolean simulate) {
            Level level = entity.level;
            if(ConfiguredItemCondition.check(smeltedInputCondition, level, input) &&
               ConfiguredItemCondition.check(smeltedOutputCondition, level, result)
            ) {
                if(!simulate) ConfiguredEntityAction.execute(smeltedEntityAction, entity);
                Mutable<ItemStack> mutable = new MutableObject<>(result.copy());
                ConfiguredItemAction.execute(smeltedOutputAction, level, mutable);
                return mutable.getValue();
            }
            return result;
        }
        
    }
    
}
