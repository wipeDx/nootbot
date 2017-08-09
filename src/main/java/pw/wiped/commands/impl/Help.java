package pw.wiped.commands.impl;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.CommandManager;
import pw.wiped.util.Config;
import pw.wiped.util.permissions.PermissionHandler;
import pw.wiped.util.permissions.Permissions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Help-command is more than just displaying the help in general.
 * It also manages the help-message of every other command.
 */
public class Help extends AbstractCommand {

    private static final String separator = "-----";

    public Help() {
        Bot.cmdMng.addCommand(new Command("Help", Permissions.MEMBER, "help"){

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                String helpString;
                String[] helpStrings;
                MessageEmbed.AuthorInfo ai;
                String desc;
                String title;
                String footer;

                if (args.length > 0 && CommandManager.getCommands().containsKey(args[0])) {
                    helpString = CommandManager.getCommands().get(args[0]).moreHelp();
                    ai = new MessageEmbed.AuthorInfo("Noot Noot!", "https://home.wiped.pw/nootbot", "", "");
                    desc = "";
                    title = CommandManager.getCommands().get(args[0]).name;
                    footer = "";
                } else {
                    ai = new MessageEmbed.AuthorInfo("Noot Noot! (Version " + Bot.VERSION+ ")", "https://home.wiped.pw/nootbot", "", "");
                    desc = "Sadly, the links are not clickable! Just for aesthetics :frowning:";
                    helpString = this.help();
                    title = "Main commands";
                    footer = "Made with ♥ by @wipeD#1889! Click the title to visit the GitHub Page of NootBot!";
                }
                helpStrings = helpString.split(separator);
                MessageEmbedImpl me = new MessageEmbedImpl();
                me.setAuthor(ai);
                me.setDescription(desc);
                me.setTitle("");
                me.setColor(Color.black);
                ArrayList<MessageEmbed.Field> fieldList = new ArrayList<>();
                for (int i = 0; i < helpStrings.length - 2; i++)
                    fieldList.add(new MessageEmbed.Field(title + '(' + (i+1) + '/' + (helpStrings.length - 2) + ')', helpStrings[i], false));
                if (args.length == 0) {
                    if (PermissionHandler.getUserPermission(e.getAuthor(), e.getGuild()) == Permissions.MODERATOR
                            || PermissionHandler.getUserPermission(e.getAuthor(), e.getGuild()) == Permissions.ADMIN) {
                        fieldList.add(new MessageEmbed.Field("Moderator Commands", helpStrings[1], false));
                        if (PermissionHandler.getUserPermission(e.getAuthor(), e.getGuild()) == Permissions.ADMIN) {
                            fieldList.add(new MessageEmbed.Field("Admin Commands", helpStrings[2], false));
                        }
                    }
                }
                me.setFields(fieldList);
                me.setFooter(new MessageEmbed.Footer(footer, "", ""));
                me.setImage(new MessageEmbed.ImageInfo("", "", 0, 0));

                e.getAuthor().openPrivateChannel().complete().sendMessage(me).complete();
                if (e.getChannelType() == ChannelType.TEXT) {
                    e.getMessage().delete().complete();
                }
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return true;
            }

            @Override
            public String help() {
                StringBuilder mainCommands = new StringBuilder();
                StringBuilder modCommands = new StringBuilder();
                StringBuilder adminCommands = new StringBuilder();
                StringBuilder current;
                int count = 1;
                ArrayList<Command> checked = new ArrayList<>();
                mainCommands.append("[.help]() - Displays this message\n\n");
                for (Command c : CommandManager.getCommands().values()) {
                    switch (c.requiredPermissions) {
                        case ADMIN: current = adminCommands; break;
                        case MODERATOR: current = modCommands; break;
                        default: current = mainCommands; count++; if (count == 10) current.append(separator);
                    }
                    if (c.equals(this) || checked.contains(c)) {
                        continue;
                    }
                    checked.add(c);
                    for (String s : c.commands) {
                        current.append("[").append(Config.getCmdPrefix()).append(s).append("]()\n");
                    }
                    current.deleteCharAt(current.length()-1).append(" - ").append(c.help()).append("\n\n");
                    if (c.requiredPermissions == Permissions.MEMBER)
                        count++;

                }
                return mainCommands.toString() + (mainCommands.toString().endsWith(separator) ? "" : separator) + modCommands.toString() + separator + adminCommands.toString();
            }

            @Override
            public String moreHelp() {
                return this.help();
            }
        });
    }

}
