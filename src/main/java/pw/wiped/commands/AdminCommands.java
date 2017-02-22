package pw.wiped.commands;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.util.CommandManager;
import pw.wiped.util.PermissionHandler;
import pw.wiped.util.Permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wipeD on 22.02.2017.
 */
public class AdminCommands {
    public AdminCommands() {
        CommandManager.addCommand(new Command("CheckPrivileges", Permissions.ADMIN,"cp",  "checkprivileges") {



            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                LOG.info(param);
                String response = "Privilege check!\n";
                if (e.getChannelType() == ChannelType.TEXT) {
                    List<Member> usersToCheck = e.getGuild().getMembersByEffectiveName(param, true);
                    if (usersToCheck.size() == 0) {
                        usersToCheck = e.getGuild().getMembersByName(param, true);
                        if (usersToCheck.size() == 0) {
                            e.getChannel().sendMessage("No such user found.").complete();
                            return;
                        }
                    }
                    for (Member m : usersToCheck) {
                        String name = (m.getNickname() == null ? m.getEffectiveName() : m.getEffectiveName() + " (" + m.getUser().getName() + ")");
                        response += name + ": " + PermissionHandler.getUserPermission(m.getUser(), e.getGuild()) + ", ";
                    }
                }
                else {
                    List<User> usersToCheck = Bot.getJDA().getUsersByName(param, true);
                    for (User u: usersToCheck) {
                        response += u.getName() + ": " + PermissionHandler.getUserPermission(u, null) + ", ";
                    }
                }


                e.getChannel().sendMessage(response.substring(0, response.length() - 2)).complete();
            }

            @Override
            public boolean called(String param, String[] args) {
                return args.length > 0;
            }

            @Override
            public void help() {

            }
        })
        .addCommand(new Command("RemoveMod", Permissions.MODERATOR, "rmmod", "removemod") {

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                if (e.getChannelType() != ChannelType.TEXT) {
                    // NO TEXT CHANNEL LUL
                }
            }

            @Override
            public boolean called(String param, String[] args) {
                return args.length > 0;
            }

            @Override
            public void help() {

            }
        });
    }
}
