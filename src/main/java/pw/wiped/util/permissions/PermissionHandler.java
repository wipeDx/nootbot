package pw.wiped.util.permissions;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import pw.wiped.Bot;
import pw.wiped.util.Config;

/**
 * PermissionHandler returns the permission of a specific user in a specific guild. (Only Bot-Permission)
 */
public class PermissionHandler {

    private static final SimpleLog LOG = SimpleLog.getLog("PermissionHandler");

    public static Permissions getUserPermission (User user, Guild guild) {

        for (User u : Config.getAdmins()) {
            if (u.getId().equals(user.getId())) {
                return Permissions.ADMIN;
            }
        }
        for (User u : Config.getBlacklisted()) {
            if (u.getId().equals(user.getId())) {
                return Permissions.BLACKLISTED;
            }
        }
        if (guild == null) {
            return Permissions.MEMBER;
        }
        LOG.info("Checking " + user.getName() + "(" + user.getId() + ") in Guild " + guild.getName() + "(" + guild.getId() + ")");

        // Check if user is member
        Member m = guild.getMember(user);
        if (m != null) {
            if (m.getRoles().contains(guild.getRoleById(Config.getAdminRoleID())))
                return Permissions.ADMIN;
            else if (m.getRoles().contains(guild.getRoleById(Config.getModeratorRoleID())))
                return Permissions.MODERATOR;
        }



        return Permissions.MEMBER;
    }
}
