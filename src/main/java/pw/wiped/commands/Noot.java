package pw.wiped.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pw.wiped.Bot;
import pw.wiped.util.CommandManager;
import pw.wiped.util.Permissions;

/**
 * Created by wiped on 2/21/17.
 */
public class Noot {

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
            public void help() {

            }
        });
    }

}
