package pw.wiped.util;

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
import java.util.Scanner;

/**
 * This class is purely the config. It can initialize itself and create itself if there's no file existent.
 */
public class Config {

    private static String token;
    private static String cmdPrefix;
    private static ArrayList<User> admins;
    private static ArrayList<User> blacklisted;
    private static String adminRoleID;
    private static String moderatorRoleID;
    private static File config;
    private static final SimpleLog LOG = SimpleLog.getLog("Config");

    private static File presetFolder;
    private static File guildFolder;
    private static File soundFolder;

    public Config (String[] args) throws IOException, ParseException {
        File configFile = new File(args.length == 0 ? "config.json" : args[0]);
        if (configFile.exists()) {
            parseConfig (configFile);
        }
        else {
            createConfig(configFile);
        }
        config = configFile;
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
        configString += "  \"token\": \"" + scan.next() + "\",\n";
        System.out.println ("###    Alright, thank you! Next, we'll need a command prefix!");
        System.out.println ("###    It defines what symbol indicates that the writing stuff is actually a command!");
        System.out.println ("###    (Our default is \"!\")");
        System.out.print   ("###    > ");
        configString += "  \"cmdPrefix\": \"" + scan.next() + "\",\n";
        System.out.println ("###    Alright, now I need your account's ID so you're an admin!");
        System.out.println ("###    You get that by right-clicking your Name and \"Copy ID\"");
        System.out.print   ("###    > ");
        configString += "  \"admins\": [\"" + scan.next() + "\"],\n";
        System.out.println ("###    For now, we'll just have you as an admin. You can add others later!");
        System.out.println ("###    Let me know your member, moderator and admin rolenames now.");
        System.out.println ("###    These will set the permissions for certain commands. If you wish");
        System.out.println ("###    to skip that, just write \"skip\". Please give me the name of your member role");
        System.out.print   ("###    > ");
        String temp = scan.next();
        // TO BE DONE TODO
        if (!temp.toLowerCase().equals("skip")) {
            configString+= "  \"memberName\": " + temp + "\",";
            System.out.println ("###    Got it! Now the moderator role name, please!");
            temp = scan.next();
            configString+= "  \"moderatorName\": " + temp + "\",";
            System.out.println ("###    Aaaand now the admin role name, please!");
            temp = scan.next();
            configString+= "  \"adminName\": " + temp + "\",";
        }
        else {
            System.out.println("###     That's okay, you'll have to set them up in your config file later though or");
            System.out.println("###     nobody but you will be able to do anything at all.");
        }

        configString += "  \"blacklisted\": []\n}";

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
        for (Object aTempJSONArray : tempJSONArray) {
            admins.add(Bot.getJDA().getUserById((String) aTempJSONArray));
        }
        tempJSONArray = (JSONArray) temp.get("blacklisted");
        blacklisted = new ArrayList<>();
        for (Object aTempJSONArray : tempJSONArray) {
            blacklisted.add(Bot.getJDA().getUserById((String) aTempJSONArray));
        }

        // Magic numbers, oh nooo
        //adminRoleID = (String) temp.get("memberName");
        moderatorRoleID = (String) temp.get("moderatorRoleID");
        adminRoleID = (String) temp.get("adminRoleID");


        presetFolder = new File("presets");
        if (!presetFolder.exists()) {
            boolean success = presetFolder.mkdir();
            if (success)
                LOG.info("Preset folder didn't exist, created successfully.");
            else
                LOG.fatal("Preset folder didn't exist, creating one failed.");
        }
        else {
            LOG.info("Preset folder exists.");
        }

        soundFolder = new File("sounds");
        if (!soundFolder.exists()) {
            boolean success = soundFolder.mkdir();
            if (success)
                LOG.info("Sound folder didn't exist, created successfully.");
            else
                LOG.fatal("Sound folder didn't exist, creating one failed.");
        }
        else {
            LOG.info("Sound folder exists.");
        }

    }

    public static String getToken() {
        return token;
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

    public static void addAdmin(User u) {
        admins.add(u);
    }

    public static File getPresetFolder() {
        return presetFolder;
    }

    public static File getSoundFolder() {
        return soundFolder;
    }

    public static String getAdminRoleID() {
        return adminRoleID;
    }

    public static String getModeratorRoleID() {
        return moderatorRoleID;
    }
}
