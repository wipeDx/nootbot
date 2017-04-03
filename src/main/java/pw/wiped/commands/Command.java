package pw.wiped.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.reflections.Reflections;
import pw.wiped.util.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wiped on 2/19/17.
 */
public abstract class Command {

    private static Reflections initCommandList = new Reflections("pw.wiped.commands.impl");

    public static void initCommands() throws IllegalAccessException, InstantiationException {
        for (Class c : initCommandList.getSubTypesOf(AbstractCommand.class)) {
            c.newInstance();
        }
    }

    public final String name;
    public final String[] commands;
    public final Permissions requiredPermissions;
    protected final SimpleLog LOG;

    public Command (String name, Permissions requiredPermissions, String... commands) {
        this.name = name;
        this.commands = commands;
        this.requiredPermissions = requiredPermissions;
        this.LOG = SimpleLog.getLog(name);
        LOG.info(name + " loaded. Following commands: " + Arrays.toString(commands));
    }

    protected StringBuilder getHelpText(int numArgs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Permissions required: ");
        sb.append(this.requiredPermissions.toString());
        sb.append("\nArguments required: ");
        sb.append(numArgs);
        sb.append("\n\n");
        return sb;
    }

    public abstract void action (String param, String[] args, MessageReceivedEvent e);
    public abstract boolean called (String param, String[] args);
    public abstract String help ();
    public abstract String moreHelp();

}
