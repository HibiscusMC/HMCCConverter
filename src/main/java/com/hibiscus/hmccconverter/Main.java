package com.hibiscus.hmccconverter;

import com.hibiscus.hmccconverter.listener.SlashCommand;
import com.hibiscus.hmccconverter.listener.SlashCommandAutoComplete;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Main {

    private static JDA jda;
    private static Properties properties;
    private static Long roleId;

    public static void main(String[] args) {
        System.out.println("Hello world!");

        properties = new Properties();
        try {
            File propertiesFile = new File(getFolder().getPath(), "config.properties");
            if (!propertiesFile.exists()) {
                System.out.println("Config file does not exist!");
                return;
            }
            FileInputStream stream = new FileInputStream(propertiesFile);
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        roleId = properties.getProperty("roleid") == null ? 0L : Long.parseLong(properties.getProperty("roleid"));

        jda = JDABuilder.createDefault(properties.getProperty("token"), GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGE_TYPING)
                .addEventListeners(new SlashCommand())
                .addEventListeners(new SlashCommandAutoComplete())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(true)
                .setActivity(Activity.playing("Converting files to HMCC!"))
                .build();

        jda.updateCommands().addCommands(Commands.slash("convert", "Converts a file to HMCC")
                .addOption(OptionType.STRING, "originalfiletype", "Original plugins type", true, true)
                .addOption(OptionType.ATTACHMENT, "file", "Original plugins file", true, false)
                .addOption(OptionType.STRING, "forceslot", "Force to add certains slot", false, true)
        ).queue();

    }

    public static JDA getJda() {
        return jda;
    }

    public static File getFolder() {
        try {
            return new File(System.getProperty("user.dir"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long getRoleId() {
        return roleId;
    }

    public static boolean requiresRole() {
        return roleId != 0L;
    }
}