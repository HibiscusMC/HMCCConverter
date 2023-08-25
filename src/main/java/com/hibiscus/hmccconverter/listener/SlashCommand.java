package com.hibiscus.hmccconverter.listener;

import com.hibiscus.hmccconverter.Main;
import com.hibiscus.hmccconverter.converter.CosmeticCoreConverter;
import com.hibiscus.hmccconverter.converter.MCCosmeticConverter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlashCommand extends ListenerAdapter {

    private static List<String> extensions = List.of("yml", "yaml");
    private static final String PRODUCT_NAME = "HMCCosmetics";
    private static final String BUY_LINK = "https://www.spigotmc.org/resources/100107/";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String forceSlot = null;
        Long roleId = Main.getRoleId();

        if (event.getName().equals("convert")) {
            if (event.getUser().isBot()) return;
            ReplyCallbackAction hook = event.deferReply(true);
            if (Main.requiresRole() && !event.getMember().getRoles().contains(event.getGuild().getRoleById(roleId))) {
                log(event.getMember().getId() + " (" + event.getMember().getEffectiveName() + ") " + "tried to convert without the correct roles!");
                event.reply("<@" + event.getMember().getId() + "> You need to have <@&" + roleId + "> role!\n\n**Purchase " + PRODUCT_NAME + "** to get access to these builds!\n\n"+ PRODUCT_NAME + ": " + BUY_LINK + "\n\n").setEphemeral(true).submit();
                return;
            }

            String type = event.getOption("originalfiletype").getAsString();
            try {
                forceSlot = event.getOption("forceslot").getAsString();
            } catch (Exception e) {

            }
            Message.Attachment attachment = event.getOption("file").getAsAttachment();
            if (!extensions.contains(attachment.getFileExtension())) {
                hook.setContent("Invalid file extension! Make sure it ends with `.yml` or `.yaml`").queue();
                log(event.getMember().getId() + " (" + event.getMember().getEffectiveName() + ") " + "tried to convert a file with invalid extension!");
                return;
            }

            try {
                File downloadFolder = new File(Main.getFolder() + "/download/");
                if (!downloadFolder.exists()) downloadFolder.mkdir();

                CompletableFuture<File> future = attachment.downloadToFile(downloadFolder + "/" + attachment.getFileName());
                File uploadFile = null;

                File file = future.get();

                // Load downloaded file
                YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(file.toPath()).build();
                CommentedConfigurationNode oldConfig;
                try {
                    oldConfig = loader.load();
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                // Create new file which will later be uploaded
                File uploadFolder = new File(Main.getFolder() + "/upload/");
                if (!uploadFolder.exists()) uploadFolder.mkdir();
                File newFile = new File(uploadFolder + "/", file.getName());
                try {
                    if (newFile.exists()) newFile.delete();
                    newFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("New File " + newFile.getAbsolutePath());

                YamlConfigurationLoader newFileLoader = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).path(newFile.toPath()).build();
                CommentedConfigurationNode newConfig;
                try {
                    newConfig = newFileLoader.load(ConfigurationOptions.defaults());
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                switch (type.toLowerCase()) {
                    case "mccosmetics" -> {
                        MCCosmeticConverter.convert(oldConfig, newConfig, file.getName(), forceSlot);
                    }
                    case "cosmeticcore" -> {
                        CosmeticCoreConverter.convert(oldConfig, newConfig, file.getName(), forceSlot);
                    }
                    default -> {
                        hook.setContent("Invalid Plugin Conversion (Try `mccosmetics` or `cosmeticcore`!").setEphemeral(true).queue();
                        log(event.getMember().getId() + " (" + event.getMember().getEffectiveName() + ") " + "tried to convert a file without a valid plugin type!");
                    }
                }

                try {
                    newFileLoader.save(newConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                uploadFile = newFile;

                System.out.println("Attachment Name " + future.get().toPath());
                if (uploadFile == null) {
                    hook.setContent("Something went wrong!").setEphemeral(true).queue();
                    file.delete();
                    return;
                }
                hook.setContent("Successfully Converted your " + type + " to an HMCCosmetics configuration! Enjoy!").setFiles(FileUpload.fromData(uploadFile)).setEphemeral(true).queue();
                file.delete();
                uploadFile.delete();
                newFile.delete();
                log(event.getMember().getId() + " (" + event.getMember().getEffectiveName() + ") " + "converted " + type + " to HMCCosmetics!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void log(String message) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String time = format.format(new Date());

        try {
            File logger = new File(Main.getFolder().getPath(), "converter.log");
            if (!logger.exists()) logger.createNewFile();

            String log = "[" + time + "] " + message + "\n";
            System.out.println(log);
            Files.write(logger.toPath(), (log).getBytes(), StandardOpenOption.APPEND);
            Main.getJda().getPresence().setActivity(Activity.playing("with " + Files.readAllLines(logger.toPath()).size() + " converted HMCC configurations!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
