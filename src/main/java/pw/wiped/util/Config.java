package pw.wiped.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pw.wiped.Bot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class is purely the config. It can initialize itself and create itself if there's no file existent.
 */
public class Config {

    private static String token;
    private static String cmdPrefix;
    private static ArrayList<User> admins;
    private static ArrayList<User> blacklisted;
    private static HashMap<String, GuildPermissions> connectedGuilds;
    private static File config;
    private static final SimpleLog LOG = SimpleLog.getLog("PermissionHandler");



    private static File guildFolder;

    public Config (String[] args) throws IOException, ParseException {
        File configFile = new File(args.length == 0 ? "config.json" : args[0]);
        if (configFile.exists()) {
            parseConfig (configFile);
        }
        else {
            createConfig(configFile);
        }
        config = configFile;
        connectedGuilds = new HashMap<>();
    }

    private void createConfig(File configFile) throws IOException, ParseException {
        Scanner scan = new Scanner(System.in);
        String configString = "{\n";
        System.out.println ("###########################################");
        System.out.println ("###    Initializing first time setup");
        System.out.println ("###    Welcome to NootBot");
        System.out.println ("###    I'll help you to set up NootBot so it works for you!");
        System.out.println ("###    First of all, I'll need your Bot-Token!");
        System.out.println ("###    You can find it on https://discordapp.com/developers");
        System.out.print   ("###    > ");
        configString += "\t\"token\": \"" + scan.next() + "\",\n";
        System.out.println ("###    Alright, thank you! Next, we'll need a command prefix!");
        System.out.println ("###    It defines what symbol indicates that the writing stuff is actually a command!");
        System.out.println ("###    (Our default is \"!\")");
        System.out.print   ("###    > ");
        configString += "\t\"cmdPrefix\": \"" + scan.next() + "\",\n";
        System.out.println ("###    Alright, now I need your account's ID so you're an admin!");
        System.out.println ("###    You get that by right-clicking your Name and \"Copy ID\"");
        System.out.print   ("###    > ");
        configString += "\t\"admins\": [\"" + scan.next() + "\"],\n";
        System.out.println ("###    For now, we'll just have you as an admin. You can add others later!");
        configString += "\t\"blacklisted\": []\n}";

        System.out.println ("###    That was the first time setup! Enjoy using Nootbot!");
        System.out.println ("###########################################");
        scan.nextLine();
        scan.close();
        IO.writeFile(configFile, configString);
        parseConfig(configFile);
    }

    private static void parseConfig (File cf) throws IOException, ParseException {
        JSONObject temp = (JSONObject) new JSONParser().parse(IO.readFile(cf.getName()));
        token = (String) temp.get("token");
        cmdPrefix = (String) temp.get("cmdPrefix");
    }

    public static void initConfig () throws IOException, ParseException {
        JSONObject temp = (JSONObject) new JSONParser().parse(IO.readFile(config.getName()));
        JSONArray tempJSONArray = (JSONArray) temp.get("admins");
        admins = new ArrayList<>();
        for (int i = 0; i < tempJSONArray.size(); i++) {
            admins.add(Bot.getJDA().getUserById((String) tempJSONArray.get(i)));
        }
        tempJSONArray = (JSONArray) temp.get("blacklisted");
        blacklisted = new ArrayList<>();
        for (int i = 0; i < tempJSONArray.size(); i++) {
            blacklisted.add(Bot.getJDA().getUserById((String) tempJSONArray.get(i)));
        }

        guildFolder = new File("guilds");
        if (!guildFolder.exists()) {
            boolean success = guildFolder.mkdir();
            if (success)
                LOG.info("Guild folder didn't exist, created successfully.");
            else
                LOG.fatal("Guild folder didn't exist, creating one failed.");
        }
        else {
            LOG.info("Guild folder exists.");
        }

    }

    public static String getToken() {
        return token;
    }

    public static void initGuild(Guild g) {

    }

    public static String getCmdPrefix() {
        return cmdPrefix;
    }

    public static ArrayList<User> getAdmins() {
        return admins;
    }

    public static ArrayList<User> getBlacklisted() {
        return blacklisted;
    }

    public static HashMap<String, GuildPermissions> getConnectedGuilds() {
        return connectedGuilds;
    }

    public static void addAdmin(User u) {
        admins.add(u);
    }

    public static File getGuildFolder() {
        return guildFolder;
    }

    public static void addGuild(Guild g, GuildPermissions guildPermissions) {
        LOG.info("Adding Guild " + g.getName() + " with ID " + g.getId() + "\" to the system.");
        connectedGuilds.put(g.getId(), guildPermissions);
    }
}
