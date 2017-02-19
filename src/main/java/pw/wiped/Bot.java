package pw.wiped;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.parser.ParseException;
import pw.wiped.util.CommandManager;
import pw.wiped.util.Config;

import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * Created by wipeD on 21.01.2017.
 */
public class Bot {

    // Setting up essential parts of the bot
    private static JDA jda;
    private static final SimpleLog LOG = SimpleLog.getLog("Main");
    public static Config config;
    public final static CommandManager cmdMng = new CommandManager();

    public static void main(String[] args) {

        LOG.info ("Welcome to NootBot (+ version)");

        // Initialize config
        try {
            config = new Config (args);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jda = new JDABuilder(AccountType.BOT).setToken(Config.getToken()).buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }

    }

    public static JDA getJDA() {
        return jda;
    }

}
