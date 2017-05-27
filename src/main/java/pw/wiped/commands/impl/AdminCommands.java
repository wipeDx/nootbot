package pw.wiped.commands.impl;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.permissions.PermissionHandler;
import pw.wiped.util.permissions.Permissions;

import java.util.List;

/**
 * AdminCommands (and actually soon-to-be ModeratorCommands) consists of AdminCommands like checking and individual's
 * privileges (which was actually just to debug) or to add / remove a moderator (bot-mod, non discord) (non functional!)
 * Or changing the game name that Nootbot's playing
 */
public class AdminCommands extends AbstractCommand {
    public AdminCommands() {
        Bot.cmdMng.addCommand(new Command("Check privileges", Permissions.ADMIN,"cp",  "checkprivileges") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                LOG.debug(param);
                if (e.isFromType(ChannelType.PRIVATE)) {
                    e.getChannel().sendMessage("You can only use that in a Channel.").complete();
                }
                else {
                    StringBuilder response = new StringBuilder();
                    response.append("Privilege check!\n");
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
                            response.append(name);
                            response.append(": ");
                            response.append(PermissionHandler.getUserPermission(m.getUser(), e.getGuild()));
                            response.append(", ");
                        }
                    } else {
                        List<User> usersToCheck = Bot.getJDA().getUsersByName(param, true);
                        for (User u : usersToCheck) {
                            response.append(u.getName());
                            response.append(": ");
                            response.append(PermissionHandler.getUserPermission(u, null));
                            response.append(", ");
                        }
                    }

                    response.delete(response.length() - 2, response.length() - 1);
                    e.getChannel().sendMessage(response.toString()).complete();
                }
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return args.length > 0;
            }

            @Override
            public String help() {
                return "checkprivileges";
            }

            @Override
            public String moreHelp() {
                return "";
            }
        })
        .addCommand(new Command("Remove moderator", Permissions.MODERATOR, "rmmod", "removemod") {

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                if (e.getChannelType() != ChannelType.TEXT) {
                    // NO TEXT CHANNEL LUL
                }
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return args.length > 0;
            }

            @Override
            public String help() {
                return "removemod";
            }

            @Override
            public String moreHelp() {
                return "";
            }
        });
    }
}
