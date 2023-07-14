<h4 align="center">Source code for HMCCConverter, a Discord Bot</h4>


HMCCConverter (HMCCC) is a discord bot that allows users to upload files that are in the format of CosmeticCore and MCCosmetics to turn them into HMCCosmetics.

## Servers with the bot

A hosted version of this bot can be used in the [Hibiscus Creative Studios Discord Server](https://discord.gg/pcm8kWrdNt).

## Using the Bot

You can do `/convert <originalCosmeticPlugin> <file> (forceslot)` command in discord to use the bot. The bot will then convert the file and send it to you.

## Running the bot

To run the bot, ensure that you have Java 17 installed. Next, create `config.properties` inside the folder the jar is located and put:

```properties
token: botToken
roleid: requiredRoleId
```

The token is used to sign into the bot, the roleid is the required id to use the bot. Put it as 0 to require no role. After that, launch bot with your preferred method.

### Logs

All logs are kept in `converter.log` file. 

### Download & Upload Folders

These two folders act as folders where the bot will store the files while using them. Once the bot has completed the conversion, it will delete all the files, saving space and respecting the privacy of the users. 

