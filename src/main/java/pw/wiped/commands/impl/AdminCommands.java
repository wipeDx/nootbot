package pw.wiped.commands.impl;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.DiscordFunctions;
import pw.wiped.util.PermissionHandler;
import pw.wiped.util.Permissions;

import java.util.List;

/**
 * AdminCommands (and actually soon-to-be ModeratorCommands) consists of AdminCommands like checking and individual's
 * privileges (which was actually just to debug) or to add / remove a moderator (bot-mod, non discord) (non functional!)
 * Or changing the game name that Nootbot's playing
 */
class AdminCommands extends AbstractCommand {
    public AdminCommands() {
        Bot.cmdMng.addCommand(new Command("Check privileges", Permissions.ADMIN,"cp",  "checkprivileges") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                LOG.info(param);
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
                }
                else {
                    List<User> usersToCheck = Bot.getJDA().getUsersByName(param, true);
                    for (User u: usersToCheck) {
                        response.append(u.getName());
                        response.append(": ");
                        response.append("PermissionHandler.getUserPermission(u, null)");
                        response.append(", ");
                    }
                }

                response.delete(response.length()-2, response.length()-1);
                e.getChannel().sendMessage(response.toString()).complete();
            }

            @Override
            public boolean called(String param, String[] args) {
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

            @SuppressWarnings("StatementWithEmptyBody")
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
            public String help() {
                return "removemod";
            }

            @Override
            public String moreHelp() {
                return "";
            }
        })
        .addCommand(new Command("Change game name", Permissions.MODERATOR, "changegame") {

            private long lastUse = 0;

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                if ((System.currentTimeMillis() - lastUse) >= 300000) {
                    System.out.println (System.currentTimeMillis() + " - " + (System.currentTimeMillis() - lastUse));
                    this.lastUse = System.currentTimeMillis();
                    DiscordFunctions.changeGamePlayed(param);
                }
                else {
                    e.getChannel().sendMessage("Sorry, you have to wait at least 5 minutes between changing game names.").complete();
                }
            }

            @Override
            public boolean called(String param, String[] args) {
                return args.length > 0;
            }

            @Override
            public String help() {
                return "Changes game name played by NootBot";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(1);
                sb.append(" - The string that you want to be displayed as a game.\n\n");
                sb.append("Changes the game name that is currently played by NootBot.\nOnly moderators can use that.");
                return sb.toString();
            }
        });
    }
}
