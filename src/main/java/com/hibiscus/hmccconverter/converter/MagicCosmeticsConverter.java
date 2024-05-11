package com.hibiscus.hmccconverter.converter;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class MagicCosmeticsConverter extends Converter {

    @Override
    public ConfigurationNode convert(CommentedConfigurationNode oldConfig, CommentedConfigurationNode newConfig, String filename, String slot) {
        if (!oldConfig.node("cosmetics").virtual()) oldConfig = oldConfig.node("cosmetics");

        for (ConfigurationNode cosmetic : oldConfig.childrenMap().values()) {
            try {
                String id = cosmetic.key().toString();
                String permission = cosmetic.node("permission").getString("");
                boolean colored = cosmetic.node("colored").getBoolean(false);
                boolean invisibleLeash = cosmetic.node("invisible-leash").getBoolean(false);
                String type = cosmetic.node("type").getString("");
                int height = cosmetic.node("height").getInt(0);
                String modelEngineModel = cosmetic.node("meg", "model").getString("");

                ConfigurationNode item = cosmetic.node("item");
                String display = item.node("display").getString("");
                String material = item.node("material").getString("");
                String oraxenItem = item.node("oraxen").getString("");
                String itemsAdderItem = item.node("item-adder").getString("");
                boolean hideAttributes = item.node("hide-attributes").getBoolean(false);
                int forMe = item.node("for-me").getInt(0);

                if (!oraxenItem.isBlank()) {
                    material = "oraxen:" + oraxenItem;
                } else if (!itemsAdderItem.isBlank()) {
                    material = "itemsadder:" + itemsAdderItem;
                }

                String color = item.node("color").getString("");
                int modelData = item.node("modeldata").getInt(-1);
                boolean unbreakable = item.node("unbreakable").getBoolean(false);
                boolean glow = item.node("glow").getBoolean(false);

                ConfigurationNode hmccosmetic = newConfig.node(id);

                if (slot != null) {
                    hmccosmetic.node("slot").set(String.class, slot);
                } else {
                    hmccosmetic.node("slot").set(String.class, typeToSlot(type));
                }

                if (!permission.isBlank()) hmccosmetic.node("permission").set(String.class, permission);
                if (colored) hmccosmetic.node("dyeable").set(Boolean.class, colored);
                if (!modelEngineModel.isBlank()) hmccosmetic.node("model").set(String.class, modelEngineModel);
                if (invisibleLeash) hmccosmetic.node("show-lead").set(Boolean.class, false);
                if (height > 0) hmccosmetic.node("height").set(Integer.class, height);

                ConfigurationNode newItem = hmccosmetic.node("item");
                newItem.node("material").set(String.class, material);
                if (!display.isBlank()) newItem.node("name").set(String.class, display);
                if (!color.isBlank()) newItem.node("color").set(String.class, color);
                if (modelData >= 0) newItem.node("model-data").set(Integer.class, modelData);
                if (unbreakable) newItem.node("unbreakable").set(Boolean.class, true);
                if (glow) newItem.node("glow").set(Boolean.class, true);
                if (hideAttributes) newItem.node("item-flags").setList(String.class, List.of("HIDE_ATTRIBUTES"));

                if (forMe > 0) {
                    ConfigurationNode firstPersonItem = hmccosmetic.node("firstperson-item");
                    firstPersonItem.node("material").set(String.class, material);
                    firstPersonItem.node("model-data").set(Integer.class, forMe);
                }
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
            case "balloon" -> {
                return "BALLOON";
            }
            case "bag" -> {
                return "BACKPACK";
            }
            default -> {
                return null;
            }
        }
    }
}
