package pw.wiped.commands.impl;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.parser.ParseException;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.permissions.Permissions;
import pw.wiped.util.PresetUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RandomCommands. Random and Randompreset
 */
public class RandomCommands extends AbstractCommand {

    public RandomCommands () {
        Bot.cmdMng.addCommand(new Command("Random", Permissions.MEMBER, "random", "rand") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                Pattern pattern = Pattern.compile("\"([^\\s\"]+[^\"]*[^\\s\"]+)\"|([^\"\\s]+)");
                Matcher matcher = pattern.matcher(param);

                List<String> options = new ArrayList<>();

                while (matcher.find()) {
                    String option = matcher.group(1);
                    option = option == null ? matcher.group(2) : option;
                    option = option.replaceAll("^\\s+|\\s+$", "");
                    if (!option.isEmpty()) {
                        options.add(option);
                    }
                }
                e.getChannel().sendMessage(options.get((int) (Math.random() * options.size()))).complete();
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return args.length > 1;
            }

            @Override
            public String help() {
                return "Selects an argument randomly and returns it.(Good for deciding!)";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(-1);
                sb.append("Selects a random argument of the given ones which could be good for deciding!");
                sb.append("\n\n.random This is Random\n\t- Could produce either \"This\", \"is\" or \"Random\".");
                sb.append("\n.rand This \"also works\" well\n\t-Could produce either \"This\", \"also works\" or \"well\".");
                sb.append("\n\nNested quotation marks don't work.");
                return sb.toString();
            }
        })
        .addCommand(new Command("Random Preset", Permissions.MEMBER, "randompreset", "rp", "randpreset") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                String response = "";
                User author = e.getAuthor();

                if (args[0].equals("save")) {
                    String arguments = param.substring(param.lastIndexOf(args[2]));
                    try {
                        response = PresetUtils.savePreset(arguments, author, args[1]);
                    } catch (IOException ev) {
                        LOG.fatal("There was a problem writing (or reading) the JSON");
                        ev.printStackTrace();
                    } catch (ParseException ev) {
                        LOG.fatal("There was a problem parsing the JSON");
                        ev.printStackTrace();
                    }
                }
                else if (args.length == 1 && !(args[0].equals("list"))) {    // !random <name>
                    try {
                        response = PresetUtils.getRandomFromPreset(args[0], author);
                    } catch (IOException ev) {
                        LOG.fatal("There was a problem reading the JSON");
                        ev.printStackTrace();
                    } catch (ParseException ev) {
                        LOG.fatal("There was a problem parsing the JSON");
                        ev.printStackTrace();
                    }
                }
                else if (args[0].equals("get") && e.isFromType(ChannelType.TEXT)) {
                    String username = param.substring(param.lastIndexOf(args[1])+args[1].length()+1);
                    List<Member> l = e.getGuild().getMembersByEffectiveName(username, true);
                    if (l.size() > 1) {
                        response = "There are more people with that name. Getting first one.\n\n";
                    }
                    try {
                        response = PresetUtils.getRandomFromPreset(args[1], l.get(0).getUser());
                    } catch (IOException ev) {
                        LOG.fatal("There was a problem reading the JSON");
                        ev.printStackTrace();
                    } catch (ParseException ev) {
                        LOG.fatal("There was a problem parsing the JSON");
                        ev.printStackTrace();
                    }
                }
                else if (args[0].equals("delete")) {
                    try {
                        response = PresetUtils.deletePreset(args[1], author);
                    } catch (IOException ev) {
                        LOG.fatal("There was a problem writing (or reading) the JSON");
                        ev.printStackTrace();
                    } catch (ParseException ev) {
                        LOG.fatal("There was a problem parsing the JSON");
                        ev.printStackTrace();
                    }
                }
                else if (args[0].equals("list")) {
                    if (e.isFromType(ChannelType.PRIVATE)) {
                        try {
                            if (args.length == 1)
                                response = PresetUtils.listPresets(author);
                            else {
                                String username = param.substring(param.lastIndexOf(args[0])+args[0].length()+1);
                                List<Member> l = e.getGuild().getMembersByEffectiveName(username, true);
                                if (l.size() > 1) {
                                    response = "There are more people with that name. Getting first one.\n\n";
                                }
                                response = PresetUtils.listPresets(l.get(0).getUser());
                            }
                        } catch (IOException ev) {
                            LOG.fatal("There was a problem reading the JSON");
                            ev.printStackTrace();
                        } catch (ParseException ev) {
                            LOG.fatal("There was a problem parsing the JSON");
                            ev.printStackTrace();
                        }
                    }
                    else {
                        response = "You can only do that in private.";
                    }
                }

                if (!(response != null && response.isEmpty())) {
                    e.getChannel().sendMessage(response).complete();
                }
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                if (args.length > 0) {
                    if (args[0].equals("save") && args.length <= 3) {
                        e.getChannel().sendMessage("You either forgot to set a name or have too few arguments.").complete();
                        return false;
                    } else if (args[0].equals("get") && args.length < 3) {
                        e.getChannel().sendMessage("You either forgot a username or a name of the preset").complete();
                        return false;
                    } else if (args[0].equals("delete") && args.length == 1) {
                        e.getChannel().sendMessage("You forgot to mention a preset to delete!").complete();
                        return false;
                    }
                }
                // Should cover list and <name> and "empty"
                return (args.length > 0);

            }

            @Override
            public String help() {
                return "Random command's functionality, but with presets for easy usage.";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(-1);
                sb.append("- First argument must be one of the following:");
                sb.append("\n\tlist, save, delete, <presetName>");

                sb.append("\n\nA wrapper around the random command to allow for presets.");

                sb.append("\n\n.randompreset list\n\t- lists your own presets");
                sb.append("\n\n.randompreset delete <presetname>\n\t- Delete <prestname> from your preset list ");
                sb.append("\n\n.randpreset <name>\n\t- Generates the random out of the preset \"<name>\"");
                sb.append("\n\n.rp list <user>\n\t- lists <username>'s presets");
                sb.append("\n\n.rp save <name> <arg1> \"<arg with spaces>\"\n\t- saves the preset");
                sb.append("\n\n.rp get <user> <presetname>\n\t- Generates random of <user>'s preset \"<presetname>\"");

                sb.append("\n\nNested quotation marks don't work.");
                sb.append("\nThis still needs some work, it might not function 100% correct");
                return sb.toString();
            }
        });
    }

}
