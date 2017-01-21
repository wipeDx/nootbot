import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.parser.ParseException;
import util.Config;

import java.io.IOException;

/**
 * Created by wipeD on 21.01.2017.
 */
public class Bot {

    // Setting up essential parts of the bot
    private static JDA jda;
    private static final SimpleLog LOG = SimpleLog.getLog("Main");
    public static Config config;

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

    }

}
