package pw.wiped.commands.impl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shuffles a list of entries and returns them in a different order!
 */
public class ShuffleCommands extends AbstractCommand {
    public ShuffleCommands() {
        Bot.cmdMng.addCommand(new Command("Shuffle", Permissions.MEMBER, "shuffle") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                String[] shuffled = shuffle(param);
                StringBuilder textToSend = new StringBuilder();
                for (String s : shuffled) {
                    textToSend.append(s).append(' ');
                }
                textToSend.replace(textToSend.length()-2, textToSend.length(), "");
                e.getChannel().sendMessage(textToSend.toString()).complete();
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return args.length > 0;
            }

            @Override
            public String help() {
                return "Shuffles all arguments";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(-1);
                sb.append("Takes an infinite amount of arguments and shuffles their order.");
                sb.append("\nWorks with \" \"! (\"A Name\" counts as one entry)");
                return sb.toString();
            }
        })
        .addCommand(new Command("Overwatch shuffle", Permissions.MEMBER, "owshuffle") {

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                String[] shuffled = shuffle(param);
                StringBuilder textToSend = new StringBuilder();
                textToSend.append("The Teamcomposition is the following:");
                for (int i = 0; i < shuffled.length; i++) {
                    switch (i) {
                        case 0:
                            textToSend.append("\n:ambulance: (Support): ");
                            break;
                        case 2:
                            textToSend.replace(textToSend.length()-2, textToSend.length(), "");
                            textToSend.append("\n:shield: (Tank): ");
                            break;
                        case 4:
                            textToSend.replace(textToSend.length()-2, textToSend.length(), "");
                            textToSend.append("\n:gun: (DPS): ");
                            break;
                        case 6:
                            textToSend.replace(textToSend.length()-2, textToSend.length(), "");
                            textToSend.append("\n:x: (Can't play): ");
                    }
                    textToSend.append(shuffled[i]).append(", ");
                }
                textToSend.replace(textToSend.length()-2, textToSend.length(), "");
                e.getChannel().sendMessage(textToSend.toString()).complete();
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return args.length > 0;
            }

            @Override
            public String help() {
                return "Creates an Overwatch team composition!";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(6, -1);
                sb.append("Takes an infinite amount of player names and shuffles them and thus creates a teamcomposition.");
                sb.append("\nIt divides them by Support, Tank and DPS\n(and \"Not Plaing\" if more than 6 names given)\n");
                sb.append("Works with \" \"! (\"A Name\" counts as one entry)");
                return sb.toString();
            }
        });
    }

    // Converts the given parameter to a String[] array
    private String[] shuffle(String param) {
        Pattern pattern = Pattern.compile("\"([^\\s\"]+[^\"]*[^\\s\"]+)\"|([^\"\\s]+)");
        Matcher matcher = pattern.matcher(param);

        ArrayList<String> order = new ArrayList<>();
        while (matcher.find()) {
            String option = matcher.group(1);
            option = option == null ? matcher.group(2) : option;
            option = option.replaceAll("^\\s+|\\s+$", "");
            if (!option.isEmpty()) {
                order.add(option);
            }
        }

        String[] array = order.toArray(new String[0]);
        // Taken from https://stackoverflow.com/a/18456998
        int index;
        String temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        return array;
    }
}
