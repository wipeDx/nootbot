package pw.wiped.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * Created by wiped on 2/19/17.
 */
public class PermissionHandler {



    public static Permissions getUserPermission (User author, Guild guild) {
        return Permissions.MEMBER;
    }

}
