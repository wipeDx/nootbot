package pw.wiped.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.reflections.Reflections;
import pw.wiped.util.Permissions;

import java.util.Arrays;

/**
 * The main structure of a command. Sets up general stuff like a logger but also initializes all commands without the
 * need of mentioning them. Reflection yeah!
 */
public abstract class Command {

    private static final Reflections initCommandList = new Reflections("pw.wiped.commands.impl");

    public static void initCommands() throws IllegalAccessException, InstantiationException {
        for (Class c : initCommandList.getSubTypesOf(AbstractCommand.class)) {
            c.newInstance();
        }
    }

    public final String name;
    public final String[] commands;
    public final Permissions requiredPermissions;
    protected final SimpleLog LOG;

    protected Command (String name, Permissions requiredPermissions, String... commands) {
        this.name = name;
        this.commands = commands;
        this.requiredPermissions = requiredPermissions;
        this.LOG = SimpleLog.getLog(name);
        LOG.info(name + " loaded. Following commands: " + Arrays.toString(commands));
    }

    protected StringBuilder getHelpText(int... numArgs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Permissions required: ");
        sb.append(this.requiredPermissions.toString());
        sb.append("\nArguments required: ");
        for (int numArg : numArgs) {
            if (numArg == -1) {

                sb.delete(sb.lastIndexOf("\nArguments required: "), sb.length() - 1);
                sb.append("\nArguments required: infinite | ");
                break;
            }
            sb.append(numArg);
            sb.append(" | ");
        }
        sb.delete(sb.length()-3, sb.length()-1);
        sb.append("\n\n");
        return sb;
    }

    public abstract void action (String param, String[] args, MessageReceivedEvent e);
    public abstract boolean called (String param, String[] args);
    public abstract String help ();
    public abstract String moreHelp();

}
