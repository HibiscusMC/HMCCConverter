package com.hibiscus.hmccconverter.converter;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class MCCosmeticConverter extends Converter {

    @Override
    public ConfigurationNode convert(CommentedConfigurationNode oldConfig, CommentedConfigurationNode newConfig, String filename, String slot) {

        if (slot == null) {
            switch (filename) {
                case "offhands.yml" -> {
                    slot = "OFFHAND";
                }
                case "backpacks.yml" -> {
                    slot = "BACKPACK";
                }
                case "hats.yml" -> {
                    slot = "HELMET";
                }
            }
        }

        for (ConfigurationNode cosmetic : oldConfig.childrenMap().values()) {
            try {
                String id = cosmetic.key().toString();
                String material = cosmetic.node("Material").getString();
                String display = cosmetic.node("Display").getString();
                int model = cosmetic.node("Model").getInt();
                List<String> description = cosmetic.node("Description").getList(String.class);

                ConfigurationNode hmccosmetic = newConfig.node(id);
                if (slot != null) hmccosmetic.node("slot").set(String.class, slot);
                ConfigurationNode item = hmccosmetic.node("item");
                item.node("material").set(String.class, material);
                item.node("name").set(String.class, display);
                item.node("model-data").set(Integer.class, model);
                item.node("lore").setList(String.class, description);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return newConfig;
    }

}
