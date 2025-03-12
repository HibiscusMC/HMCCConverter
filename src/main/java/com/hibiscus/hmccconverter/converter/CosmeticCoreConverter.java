package com.hibiscus.hmccconverter.converter;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class CosmeticCoreConverter extends Converter {

    private static final List<String> baseMaterial = List.of("paper", "leather_horse_armor", "potion", "feather");

    @Override
    public ConfigurationNode convert(CommentedConfigurationNode oldConfig, CommentedConfigurationNode newConfig, String filename, String slot) {
        if (!oldConfig.node("cosmetics").virtual()) oldConfig = oldConfig.node("cosmetics");

        for (ConfigurationNode cosmetic : oldConfig.childrenMap().values()) {
            try {
                String id = cosmetic.key().toString();
                String type = cosmetic.node("type").getString("");
                boolean dyable = cosmetic.node("dye", "enabled").getBoolean();
                String material = null;
                int modelData = -1;

                ConfigurationNode model = cosmetic.node("model");
                for (ConfigurationNode modelNode : model.childrenMap().values()) {
                    String item = modelNode.getString();
                    if (item != null) {
                        String[] itemSplit = item.split(":");
                        if (itemSplit.length == 2) {
                            if (itemSplit[0].equalsIgnoreCase("minecraft")) {
                                material = itemSplit[1];
                                // MINECRAFT:ITEMID
                            }
                            if (baseMaterial.contains(itemSplit[0])) {
                                material = itemSplit[0];
                                modelData = Integer.parseInt(itemSplit[1]);
                                // PAPER:MODELID
                            } else {
                                material = "itemsadder:" + item;
                            }
                        }
                    }
                }

                String display = cosmetic.node("display_name").getString();

                ConfigurationNode hmccosmetic = newConfig.node(id);

                if (slot != null) {
                    hmccosmetic.node("slot").set(String.class, slot);
                } else {
                    hmccosmetic.node("slot").set(String.class, typeToSlot(type));
                }
                hmccosmetic.node("dyeable").set(Boolean.class, dyable);
                ConfigurationNode item = hmccosmetic.node("item");
                item.node("material").set(String.class, material);
                item.node("name").set(String.class, display);
                if (modelData >= 0) item.node("model-data").set(Integer.class, modelData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return newConfig;
    }

    private static String typeToSlot(String type) {
        switch (type.toLowerCase()) {
            case "hat" -> {
                return "HELMET";
            }
            case "body_entity", "body_item" -> {
                return "BACKPACK";
            }
            case "balloon_entity" -> {
                return "BALLOON";
            }
            default -> {
                return null;
            }
        }
    }

}
