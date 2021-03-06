package pw.wiped;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.parser.ParseException;
import pw.wiped.commands.Command;
import pw.wiped.util.CommandManager;
import pw.wiped.util.Config;
import pw.wiped.util.permissions.GuildPermissions;

import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * The main class of the Bot. Does all the good things. Initializing everything, establishing the connection, loading
 * the config. You name it!
 */
public class Bot {

    public static final String VERSION = "0.1 Beta RC";
    private static final SimpleLog LOG = SimpleLog.getLog("Main");
    public final static CommandManager cmdMng = new CommandManager();


    private static JDA jda;

    public static void main(String[] args) {

        LOG.info ("Welcome to NootBot " + VERSION);

        // Initialize config
        try {
            new Config(args);
            jda = new JDABuilder(AccountType.BOT).setToken(Config.getToken()).buildBlocking();
            Config.initConfig();    // initialize Admins and Blacklisted
            // Register connected Guilds
            for (Guild g: jda.getGuilds()) {
                LOG.info("Registering guild " +g.getName()+ " with ID \"" + g.getId() + "\"");
                Config.addGuild(g, new GuildPermissions(g));
            }
        } catch (IOException | ParseException | InterruptedException | RateLimitedException | LoginException e) {
            e.printStackTrace();
        }

        jda.addEventListener(new BotListener());

        // Register Commands

        try {
            Command.initCommands();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }


    }

    public static JDA getJDA() {
        return jda;
    }

}
