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

/**
 * Created by wipeD on 22.02.2017.
 */
public class GuildPermissions {

    private Guild guild;
    private ArrayList<User> mods;
    private ArrayList<User> members;
    private ArrayList<User> blacklisted;
    private Permissions voicePermissions;
    private File guildFile;
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
        for (int i = 0; i < temp.size(); i++) {
            mods.add(Bot.getJDA().getUserById((String) temp.get(i)));
        }
        temp = (JSONArray) guildContent.get("members");
        for (int i = 0; i < temp.size(); i++) {
            members.add(Bot.getJDA().getUserById((String) temp.get(i)));
        }
        temp = (JSONArray) guildContent.get("blacklisted");
        for (int i = 0; i < temp.size(); i++) {
            blacklisted.add(Bot.getJDA().getUserById((String) temp.get(i)));
        }
        this.voicePermissions = Permissions.valueOf((String) guildContent.get("voicePermissions"));

        return guildContent;
    }

}
