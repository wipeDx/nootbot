package pw.wiped.commands.impl;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.SimpleLog;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.DiscordFunctions;
import pw.wiped.util.Permissions;

import java.util.List;

/**
 * ModeratorCommands is a collection of commands that can be used by moderators.
 * "Say", "Change Game Name", "Clear messages"
 */
public class ModeratorCommands extends AbstractCommand {

    public ModeratorCommands () {
        Bot.cmdMng.addCommand(new Command("Change game name", Permissions.MODERATOR, "changegame") {

            private long lastUse = 0;

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                if ((System.currentTimeMillis() - lastUse) >= 300000) {
                    System.out.println (System.currentTimeMillis() + " - " + (System.currentTimeMillis() - lastUse));
                    this.lastUse = System.currentTimeMillis();
                    DiscordFunctions.changeGamePlayed(param);
                }
                else {
                    e.getChannel().sendMessage("Sorry, you have to wait at least 5 minutes between changing game names.").complete();
                }
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return args.length > 0;
            }

            @Override
            public String help() {
                return "Changes game name played by NootBot";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(1);
                sb.append(" - The string that you want to be displayed as a game.\n\n");
                sb.append("Changes the game name that is currently played by NootBot.\nOnly moderators can use that.");
                return sb.toString();
            }
        })
        .addCommand(new Command("Clear messages", Permissions.MODERATOR, "clear", "cl") {

            private int amount;

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                List<Message> list = e.getTextChannel().getHistory().retrievePast(amount + 1).complete();
                if (LOG.getLevel() == SimpleLog.Level.DEBUG)
                    for (Message m : list)
                        LOG.debug("Deleting message: " + m.getContent());
                e.getTextChannel().deleteMessages(list).complete();
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                if (args.length == 0)
                    amount = 1;
                else
                    amount = Integer.parseInt(args[0]);
                return amount <= 500 && amount >= 1;
            }

            @Override
            public String help() {
                return "Clears the last <amount> messages. Use with caution!";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(1);
                sb.append(" - The amount of messages to delete (Max 500).\n\n");
                sb.append("Clears messages very easily.\nOnly moderators can use that.\nUse with caution, this action is inreversable.\nMaximum of 500 messages at once.");
                return sb.toString();
            }
        })
        .addCommand(new Command("Say", Permissions.MODERATOR, "say") {

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                List<TextChannel> list = e.getGuild().getTextChannels();
                if (LOG.getLevel() == SimpleLog.Level.DEBUG)
                    for (TextChannel t : list)
                        LOG.debug("Listing message: " + t.getName());

                if (args[0].startsWith("#"))
                    args[0] = args[0].replace("#", "");
                LOG.debug(args[0]);

                for (TextChannel t : list) {
                    if (args[0].equals(t.getName())) {
                        String msg = param.replace((param.startsWith("#") ? "#" : "") +t.getName()+" ", "");
                        t.sendMessage(msg).complete();
                        return;
                    }
                }
                e.getChannel().sendMessage("That Textchannel does not exist or is unknown to me.").complete();
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return args.length >= 2;
            }

            @Override
            public String help() {
                return "NootBot says something in another channel";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(2);
                sb.append(" - [1] Message: The message to be said.\n - [2] TextChannel: The textchannel in which it will be said.\n\n");
                sb.append("Says whatever you like in a different channel.\nOnly moderators can use that.\nDon't abuse it too much, but it's your rights, heh.");
                return sb.toString();
            }
        });
    }

}
