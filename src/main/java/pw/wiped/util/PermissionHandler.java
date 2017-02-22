package pw.wiped.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wiped on 2/19/17.
 */
public class PermissionHandler {

    public static Permissions getUserPermission (User user, Guild guild) {
        for (User u : Config.getAdmins()) {
            if (u.getId().equals(user.getId())) {
                return Permissions.ADMIN;
            }
        }
        if (guild == null) {
            return Permissions.NOTFOUND;
        }
        GuildPermissions gp = Config.getConnectedGuilds().get(guild.getId());
        for (User u : gp.getMods()) {
            if (u.getId().equals(user.getId())) {
                return Permissions.MODERATOR;
            }
        }
        for (User u : gp.getMembers()) {
            if (u.getId().equals(user.getId())) {
                return Permissions.MEMBER;
            }
        }
        for (User u : gp.getBlacklisted()) {
            if (u.getId().equals(user.getId())) {
                return Permissions.BLACKLISTED;
            }
        }
        return Permissions.GUEST;
    }
}
