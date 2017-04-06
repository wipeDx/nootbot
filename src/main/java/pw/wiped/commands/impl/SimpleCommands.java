package pw.wiped.commands.impl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SimpleCommands that only compute a little and throw out a response right after.
 * These commands are: Roll the dice, Flip a coin and throwing out a random part of an argument list.
 */
class SimpleCommands extends AbstractCommand{

    public SimpleCommands () {
        Bot.cmdMng.addCommand(new Command("Roll", Permissions.GUEST, "roll", "rtd", "dice") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                int min = 0;
                int max = 100;
                if (!param.equals("") && !param.contains("-") && args.length > 1) {
                    try {
                        min = Integer.parseInt(args[0]);
                        max = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                    }
                } else if (args.length == 1) {
                    try {
                        max = Integer.parseInt(args[0]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (param.contains("-") && !param.startsWith("-")) {
                    try {
                        String[] subString = param.split("-");
                        min = Integer.parseInt(subString[0]);
                        max = Integer.parseInt(subString[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }

                min = Math.abs(min);
                max = Math.abs(max);

                if (max > 999999) {
                    max = 999999;
                }
                if (min > 999999) {
                    min = 0;
                }
                if (max < min) {
                    int t = max;
                    max = min;
                    min = t;
                } else if (max == min) {
                    max++;
                }
                int rollsy = (int) ((Math.random() * (max - min + 1)) + min);
                e.getChannel().sendMessage("You rolled a " + rollsy + "! (" + min + " - " + max + ")").complete();
            }

            @Override
            public boolean called(String param, String[] args) {
                return true;
            }

            @Override
            public String help() {
                return "Rolls the dice to solve who gets the loot!";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(0, 1, 2);
                sb.append("Rolls a dice in an old-fashioned manner.");
                sb.append("\n\n.roll\t\t\t- rolls between 0 and 100");
                sb.append("\n.rtd 5-10\t\t- rolls between 5 and 10");
                sb.append("\n.dice 0 25\t\t- rolls between 0 and 25");
                sb.append("\n\nNegative numbers won't work, they are being abstracted (-1 => |-1| = 1).");
                sb.append("\nIf the second number is bigger, the numbers will be switched around.");
                sb.append("\nThe maximum number is 999.999, everything above will be set to that.");
                return sb.toString();
            }
        }).addCommand(new Command("Random", Permissions.MEMBER, "random", "rand") {
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
            public boolean called(String param, String[] args) {
                return args.length > 1;
            }

            @Override
            public String help() {
                return "Selects an argument randomly and returns it.(Good for deciding!)";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(-1);
                sb.append("Selects a random argument given which could be good for deciding!");
                sb.append("\n\n.random This is Random\n\t- Could produce either \"This\", \"is\" or \"Random\".");
                sb.append("\n.rand This \"also works\" well\n\t-Could produce either \"This\", \"also works\" or \"well\".");
                sb.append("\n\nNested quotation marks don't work.");
                return sb.toString();
            }
        }).addCommand(new Command("Flip", Permissions.GUEST, "flip", "flop", "flipflop", "flopflip") {

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                int rollsy = (int) (Math.random() * 2);
                int rollsyTWO = (int) (Math.random() * 4);

                String boop;

                switch (rollsyTWO) {
                    case 0:
                        boop = "flip";
                        break;
                    case 1:
                        boop = "flop";
                        break;
                    case 2:
                        boop = "flipflop";
                        break;
                    case 3:
                        boop = "flopflip";
                        break;
                    default:
                        boop = "Something went horribly wrong";
                        break;
                }
                e.getChannel().sendMessage("You " + boop + "ped a coin: " + (rollsy == 1 ? "Tails" : "Heads")).complete();
            }

            @Override
            public boolean called(String param, String[] args) {
                return true;
            }

            @Override
            public String help() {
                return "Flips a coin.";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(0);
                sb.append("Not much to say here, flips a coin.");
                return sb.toString();
            }
        });
    }

}
