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
import pw.wiped.util.permissions.Permissions;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Help-command is more than just displaying the help in general.
 * It also manages the help-message of every other command.
 */
public class Help extends AbstractCommand {


    public Help() {
        Bot.cmdMng.addCommand(new Command("Help", Permissions.GUEST, "help"){

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                String helpString;
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
                    footer = "Made with ♥ by wipeD! Click the title to visit the GitHub Page of NootBot!";
                }
                MessageEmbedImpl me = new MessageEmbedImpl();
                me.setAuthor(ai);
                me.setDescription(desc);
                me.setTitle("");
                me.setColor(Color.black);
                ArrayList<MessageEmbed.Field> fieldList = new ArrayList<>();
                fieldList.add(new MessageEmbed.Field(title, helpString, true));
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
                StringBuilder sb = new StringBuilder();
                ArrayList<Command> checked = new ArrayList<>();
                sb.append("[.help]() - Displays this message\n\n");
                for (Command c : CommandManager.getCommands().values()) {
                    if (c.equals(this) || checked.contains(c)) {
                        continue;
                    }
                    checked.add(c);
                    for (String s : c.commands) {
                        sb.append("[");
                        sb.append(Config.getCmdPrefix());
                        sb.append(s);
                        sb.append("]()\n");
                    }
                    sb.deleteCharAt(sb.length()-1);
                    sb.append(" - ");
                    sb.append(c.help());
                    sb.append("\n\n");
                }
                return sb.toString();
            }

            @Override
            public String moreHelp() {
                return this.help();
            }
        });
    }

}
