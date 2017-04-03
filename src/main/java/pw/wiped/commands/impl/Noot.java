package pw.wiped.commands.impl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.Permissions;

/**
 * Created by wiped on 2/21/17.
 */
public class Noot extends AbstractCommand {

    public Noot() {
        Bot.cmdMng.addCommand(new Command("Noot", Permissions.GUEST, "noot", "nootnoot") {

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                e.getChannel().sendMessage("Noot Noot! :penguin:").complete();

            }

            @Override
            public boolean called(String param, String[] args) {
                return true;
            }

            @Override
            public String help() {
                return "Returns a noot!";
            }

            @Override
            public String moreHelp() {
                return "";
            }
        }).addCommand(new Command("Buff", Permissions.GUEST, "buff", "buffpingu") {

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                e.getChannel().sendMessage(":black_circle:-:muscle::skin-tone-1::penguin::muscle::skin-tone-1:-:black_circle:  BUFFPINGU").complete();
            }

            @Override
            public boolean called(String param, String[] args) {
                return true;
            }

            @Override
            public String help() {
                return "Displays a funny message!";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(0);
                sb.append("Displays a message that's very accurate to this bot's owner's serious level.");
                return sb.toString();
            }
        });
    }


}
