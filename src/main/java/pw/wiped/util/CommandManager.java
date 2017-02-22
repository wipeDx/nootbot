package pw.wiped.util;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.SimpleLog;

import pw.wiped.Bot;
import pw.wiped.commands.Command;
import pw.wiped.util.Permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * Created by wiped on 2/19/17.
 */
public class CommandManager {
    private final SimpleLog LOG = SimpleLog.getLog("CommandManager");
    private static HashMap<String, Command> commands = new HashMap<>();

    public CommandContainer parse (String rw, MessageReceivedEvent e) {
        // Splitting everything accordingly and setting mandatory variables like author, arguments etc.
        ArrayList<String> split = new ArrayList<>();
        String beheaded = rw.replaceFirst(Config.getCmdPrefix(), "");
        String[] splitBeheaded = beheaded.split(" ");
        Collections.addAll(split, splitBeheaded);
        String invoke = split.get(0);
        String param = beheaded.replaceFirst(invoke+" ", "");
        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);
        User author = e.getAuthor();
        Permissions givenPermissions = PermissionHandler.getUserPermission(e.getAuthor(), e.getGuild());

        return new CommandContainer(param, rw, invoke, args, givenPermissions, e, author);
    }

    public class CommandContainer {
        public final String param;
        public final String raw;
        public final Command command;
        public final String invoke;
        public final String[] args;
        public final MessageReceivedEvent e;
        public final User author;
        public final Permissions givenPermissions;

        public CommandContainer (String param, String rw, String invoke, String[] args, Permissions givenPermissions, MessageReceivedEvent e, User author) {
            this.param = param;
            this.raw = rw;
            this.invoke = invoke;
            this.command = commands.get(invoke);
            this.args = args;
            this.e = e;
            this.givenPermissions = givenPermissions;
            this.author = author;
        }
    }

    public void handleCommand(CommandContainer cmd) {
        for (User u: Config.getBlacklisted()) {
            if (u.getId().equals(cmd.author.getId())) {
                LOG.info(cmd.author.getName() + " tried to use " + cmd.invoke + ", but is blacklisted.");
                return;
            }
        }

        boolean enoughPermission = PermissionHandler.getUserPermission(cmd.author, cmd.e.getGuild()).ordinal() >= cmd.command.requiredPermissions.ordinal();
        boolean sufficientArgs = cmd.command.called(cmd.param, cmd.args);

        if (enoughPermission && sufficientArgs) {
            cmd.command.action(cmd.param, cmd.args, cmd.e);
            LOG.info(cmd.author.getName() + " used " + cmd.invoke + (cmd.e.isFromType(ChannelType.PRIVATE) ? "(Private)": ""));
        }
        else if(!enoughPermission && sufficientArgs){
            LOG.info(cmd.author.getName() + " tried to use " + cmd.invoke + (cmd.e.isFromType(ChannelType.PRIVATE) ? "(Private)": "") + ", but didn't have enough permission.");
        }

    }

    public CommandManager addCommand(Command cmd) {
        for (String s : cmd.commands) {
            commands.put(s, cmd);
        }
        return Bot.cmdMng;

    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }
}
