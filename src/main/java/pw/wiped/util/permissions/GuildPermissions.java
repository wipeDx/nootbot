package pw.wiped.util.permissions;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pw.wiped.Bot;
import pw.wiped.util.Config;
import pw.wiped.util.IO;
import pw.wiped.util.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Manages the Permissions on the guild-part. These can be set per-guild.
 */
public class GuildPermissions {

    private final Guild guild;
    private final ArrayList<User> mods;
    private final ArrayList<User> members;
    private final ArrayList<User> blacklisted;
    private Permissions voicePermissions;
    private final File guildFile;
    private JSONObject content;
    private static SimpleLog LOG = SimpleLog.getLog("GuildPermissions");

    public Guild getGuild() {
        return guild;
    }

    public ArrayList<User> getMods() {
        return mods;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public ArrayList<User> getBlacklisted() {
        return blacklisted;
    }

    public Permissions getVoicePermissions() {
        return voicePermissions;
    }

    public File getGuildFile() {
        return guildFile;
    }

    public void addMod (User u) throws IOException {
        mods.add(u);
        JSONArray temp = (JSONArray) this.content.get("mods");
        temp.add(u.getId());
        IO.writeFile(this.guildFile, this.content.toString());
    }

    public void addMember (User u) throws IOException {
        members.add(u);
        JSONArray temp = (JSONArray) this.content.get("members");
        temp.add(u.getId());
        IO.writeFile(this.guildFile, this.content.toString());
    }

    public void addBlacklisted (User u) throws IOException {
        blacklisted.add(u);
        JSONArray temp = (JSONArray) this.content.get("blacklisted");
        temp.add(u.getId());
        IO.writeFile(this.guildFile, this.content.toString());
    }

    public void setVoicePermissions (Permissions permissions) throws IOException {
        this.content.replace("voicePermissions", permissions.toString());
        IO.writeFile(this.guildFile, this.content.toString());
    }

    public GuildPermissions (Guild g) throws IOException, ParseException {
        this.guildFile = new File (Config.getGuildFolder().getName() +"/"+ g.getId() + ".json");
        this.guild = g;
        this.mods = new ArrayList<>();
        this.members = new ArrayList<>();
        this.blacklisted = new ArrayList<>();
        if (guildFile.exists()) {
            this.content = readGuildContent (guildFile);
        }
        else {
            this.content = createGuildContent (guildFile);

        }
    }

    private JSONObject createGuildContent(File guildFile) throws IOException {
        JSONObject guildContent = new JSONObject();
        guildContent.put("mods", new JSONArray());
        guildContent.put("members", new JSONArray());
        guildContent.put("blacklisted", new JSONArray());
        guildContent.put("voicePermissions", "MODERATOR");
        IO.writeFile(guildFile, guildContent.toString());
        return guildContent;
    }

    private JSONObject readGuildContent (File guildFile) throws IOException, ParseException {
        JSONObject guildContent = (JSONObject) new JSONParser().parse(IO.readFile(guildFile.getAbsolutePath()));
        JSONArray temp = (JSONArray) guildContent.get("mods");
        for (Object aTemp : temp) {
            mods.add(Bot.getJDA().getUserById((String) aTemp));
        }
        temp = (JSONArray) guildContent.get("members");
        for (Object aTemp : temp) {
            members.add(Bot.getJDA().getUserById((String) aTemp));
        }
        temp = (JSONArray) guildContent.get("blacklisted");
        for (Object aTemp : temp) {
            blacklisted.add(Bot.getJDA().getUserById((String) aTemp));
        }
        this.voicePermissions = Permissions.valueOf((String) guildContent.get("voicePermissions"));

        return guildContent;
    }

}
