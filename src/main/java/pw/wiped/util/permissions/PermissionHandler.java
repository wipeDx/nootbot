package pw.wiped.util.permissions;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import pw.wiped.util.Config;

/**
 * PermissionHandler returns the permission of a specific user in a specific guild. (Only Bot-Permission)
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