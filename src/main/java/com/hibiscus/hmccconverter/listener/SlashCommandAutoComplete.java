package com.hibiscus.hmccconverter.listener;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlashCommandAutoComplete extends ListenerAdapter {

    private final String[] words = new String[]{
            "MCCosmetics",
            "CosmeticCore",
            "MagicCosmetics"
    };
    private final String[] slots = new String[]{
            "HELMET",
            "CHESTPLATE",
            "LEGGINGS",
            "BOOTS",
            "MAINHAND",
            "OFFHAND",
            "BACKPACK",
            "BALLOON",
            "EMOTE"
    };

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        System.out.println(event.getName());
        if (!event.getName().equalsIgnoreCase("convert")) {
            return;
        }

        if (event.getFocusedOption().getName().equals("originalfiletype")) {
            List<Command.Choice> options = Stream.of(words)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
        if (event.getFocusedOption().getName().equals("forceslot")) {
            List<Command.Choice> options = Stream.of(slots)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
