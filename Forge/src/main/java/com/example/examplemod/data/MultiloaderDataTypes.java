package com.example.examplemod.data;

import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import net.minecraftforge.common.util.Lazy;

/**
 * Lazily loaded SerializableDataTypes for Forge, as early access will cause NPE by far
 */
public class MultiloaderDataTypes {
    public static final Lazy<SerializableDataType<ConfiguredBiEntityCondition<?, ?>>> BIENTITY_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBiEntityCondition.class), ConfiguredBiEntityCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredBiEntityCondition<?, ?>>> BIOME_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBiEntityCondition.class), ConfiguredBiEntityCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredBiEntityCondition<?, ?>>> BLOCK_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBiEntityCondition.class), ConfiguredBiEntityCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredDamageCondition<?, ?>>> DAMAGE_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredDamageCondition.class), ConfiguredDamageCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredEntityCondition<?, ?>>> ENTITY_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredEntityCondition.class), ConfiguredEntityCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredFluidCondition<?, ?>>> FLUID_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredFluidCondition.class), ConfiguredFluidCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredItemCondition<?, ?>>> ITEM_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredItemCondition.class), ConfiguredItemCondition.CODEC));

    public static final Lazy<SerializableDataType<ConfiguredBiEntityAction<?, ?>>> BIENTITY_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBiEntityAction.class), ConfiguredBiEntityAction.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredBlockAction<?, ?>>> BLOCK_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBlockAction.class), ConfiguredBlockAction.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredEntityAction<?, ?>>> ENTITY_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredEntityAction.class), ConfiguredEntityAction.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredItemAction<?, ?>>> ITEM_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredItemAction.class), ConfiguredItemAction.CODEC));

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>)aClass;
    }
}
