package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class is purely the config. It can initialize itself and create itself if there's no file existent.
 */
public class Config {

    private static String token;
    private static char cmdPrefix;
    private static String[] admins;
    private static String[] blacklisted;
    private static String[] connectedGuilds;

    public Config (String[] args) throws IOException, ParseException {
        File configFile = new File(args.length == 0 ? "config.json" : args[0]);
        if (configFile.exists()) {
            parseConfig (configFile);
        }
        else {
            createConfig(configFile);
        }
    }

    private void createConfig(File configFile) throws IOException {
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
    }

    private static void parseConfig (File cf) throws IOException, ParseException {
        JSONObject temp = (JSONObject) new JSONParser().parse(IO.readFile(cf.getName()));
        token = (String) temp.get("token");
        cmdPrefix = (char) temp.get("cmdPrefix");
        JSONArray tempJSONArray = (JSONArray) temp.get("admins");
        admins = new String[tempJSONArray.size()];
        for (int i = 0; i < tempJSONArray.size(); i++) {
            admins[i] = (String) tempJSONArray.get(i);
        }
        tempJSONArray = (JSONArray) temp.get("blacklisted");
        blacklisted = new String[tempJSONArray.size()];
        for (int i = 0; i < tempJSONArray.size(); i++) {
            blacklisted[i] = (String) tempJSONArray.get(i);
        }
    }

}
