package pw.wiped.util;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for the preset random functionality.
 */
@SuppressWarnings("WeakerAccess")
public class PresetUtils {

    /**
     * The LOG for this class
     */
    private static final SimpleLog LOG = SimpleLog.getLog("PresetUtils");

    /**
     * JSONParser that parses the JSON files
     */
    private static final JSONParser jsonParser = new JSONParser();

    /**
     * Saves a preset specified through the values, the User and the name.
     * @param preset The preset values
     * @param author The User object that "owns" this preset
     * @param presetName The name of this preset
     * @return A String that is equal to the response.
     * @throws IOException If the File wasn't fount for example or there was a read error.
     * @throws ParseException Whenever there was a JSON parse error.
     */
    public static String savePreset (String preset, User author, String presetName) throws IOException, ParseException {
        if (presetName.equals("save") || presetName.equals("list") || presetName.equals("delete") || presetName.equals("get"))
            return "Don't use a keyword as a name.";
        JSONObject json;
        File jsonFile = readFile(author);
        if (jsonFile != null) {
            json = (JSONObject) jsonParser.parse(new FileReader(jsonFile));
        }
        else {
            json = new JSONObject();
            if (!createFile(author)) {
                return "Creating your savefile was unsuccessful. Contact the admin!";
            }
            jsonFile = readFile(author);
        }
        if (json.containsKey(presetName))
            return presetName + " already exists!";

        Pattern pattern = Pattern.compile("\"([^\\s\"]+[^\"]*[^\\s\"]+)\"|([^\"\\s]+)");// Filtering the options out of preset
        Matcher matcher = pattern.matcher(preset);

        JSONObject presetPart = new JSONObject();
        JSONArray presetOptions = new JSONArray();
        String presetString = "";

        while (matcher.find()) {
            String option = matcher.group(1);
            option = option == null ? matcher.group(2) : option;
            option = option.replaceAll("^\\s+|\\s+$", "");
            if (!option.isEmpty()) {
                //noinspection unchecked
                presetOptions.add(option);
                presetString += option + ", ";
                LOG.log(SimpleLog.Level.DEBUG, "Added " + option + " to presetName " + presetName);
            }
        }

        //noinspection unchecked
        presetPart.put("PresetString", presetString.substring(0, presetString.length()-2));
        //noinspection unchecked
        presetPart.put("PresetArray", presetOptions);
        //noinspection unchecked
        json.put(presetName, presetPart);

        writeJSON(json, jsonFile);
        return presetName + " successfully saved!";
    }

    /**
     * Gets a random preset from the User's preset named presetName
     * @param presetName The name of the preset to get the random from.
     * @param author The Userobject of the preset to get the random from.
     * @return A String that is equal to the response.
     * @throws IOException If the File wasn't fount for example or there was a read error.
     * @throws ParseException Whenever there was a JSON parse error.
     */
    public static String getRandomFromPreset (String presetName, User author) throws IOException, ParseException {
        File jsonFile = readFile(author);
        if (!(jsonFile != null && jsonFile.exists())) return "This user does not exist!";
        JSONObject json = (JSONObject) jsonParser.parse(new FileReader(jsonFile));
        if (!json.containsKey(presetName)){
            return "This preset does not exist.";
        }
        JSONObject actualPreset = (JSONObject) json.get(presetName);
        JSONArray options = (JSONArray) actualPreset.get("PresetArray");
        String optionString = (String) actualPreset.get("PresetString");

        return "Preset \"" + presetName + "\" by " + author.getName() + ":\n" +
                "Options: " + optionString + "\n" +
                "Result: " + options.get((int) (Math.random() * options.size()));
    }

    /**
     * Deletes a preset
     * @param presetName Presetname to delete.
     * @param author Userobject to delete preset from.
     * @return A String that is equal to the response.
     * @throws IOException If the File wasn't fount for example or there was a read error.
     * @throws ParseException Whenever there was a JSON parse error.
     */
    public static String deletePreset (String presetName, User author) throws IOException, ParseException {
        File jsonFile = readFile(author);
        if (jsonFile == null) {
            return null;
        }
        JSONObject json = (JSONObject) jsonParser.parse(new FileReader(jsonFile));    //The work of IntelliJ Idea's code inspector!
        if (!json.containsKey(presetName)){
            return "This preset does not exist.";
        }
        json.remove(presetName);
        writeJSON (json, jsonFile);
        return "Successfully deleted the preset.";
    }

    /**
     * Lists the preset of author.
     * @param author User object to list the preset of.
     * @return A String that is equal to the response.
     * @throws IOException If the File wasn't fount for example or there was a read error.
     * @throws ParseException Whenever there was a JSON parse error.
     */
    public static String listPresets (User author) throws IOException, ParseException {
        File yeyFile = readFile(author);
        if (yeyFile == null) {
            return "There are no presets.";
        }
        JSONObject json = (JSONObject) jsonParser.parse(new FileReader(yeyFile));
        StringBuilder returny = new StringBuilder();
        returny.append("Listing presets of ").append(author.getName()).append(": \n");
        //noinspection unchecked
        json.forEach((k, v) -> {
            JSONObject valueObject = (JSONObject) v;
            returny.append(k).append(": ").append("\"").append(valueObject.get("PresetString")).append("\"\n");
        });

        return returny.toString();
    }

    /**
     * Writes the json to an actual file.
     * @param json Content of the JSON.
     * @param file File to write into.
     * @throws IOException When the file wasn't writable or sth.
     */
    public static void writeJSON (JSONObject json, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);   // Writing the JSONFile
        fileWriter.write(json.toJSONString());
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * Reads the file of Author
     * @param author User object to read the file from.
     * @return The File object that got read.
     * @throws IOException If reading the file fails.
     */
    public static File readFile (User author) throws IOException {
        StringBuilder path = new StringBuilder();
        path.append(Config.getPresetFolder().getCanonicalPath()).append(File.separator).append(author.getId()).append(".json");
        File actualFile = new File(path.toString());
        if (actualFile.exists())
            return actualFile;
        else
            return null;
    }

    /**
     * Creating the file.
     * @param author To get the ID from.
     * @return True if it worked, false if not.
     * @throws IOException If it didnt work.
     */
    public static boolean createFile (User author) throws IOException {
        StringBuilder path = new StringBuilder();
        path.append(Config.getPresetFolder().getCanonicalPath()).append(File.separator).append(author.getId()).append(".json");
        return new File(path.toString()).createNewFile();
    }
}
