package pw.wiped;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.parser.ParseException;
import pw.wiped.util.CommandManager;
import pw.wiped.util.Config;
import pw.wiped.util.GuildPermissions;

import java.io.IOException;

class BotListener extends ListenerAdapter {

    private final SimpleLog LOG = SimpleLog.getLog("BotListener");

    @Override
    public void onMessageReceived (MessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith(Config.getCmdPrefix()) && !event.getAuthor().isBot() && CommandManager.getCommands().containsKey(event.getMessage().getContent().replaceFirst(Config.getCmdPrefix(), "").split(" ")[0])) {
            Bot.cmdMng.handleCommand(Bot.cmdMng.parse(event.getMessage().getContent(), event));
        }
    }



    @Override
    public void onReady (ReadyEvent event) {
        //Bot.log ("status", "Logged in as: " + event.getJDA().getSelfInfo().getUsername());
    }

    @Override
    public void onGuildJoin (GuildJoinEvent gje) {
        try {
            new GuildPermissions(gje.getGuild());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        LOG.info("Joined guild " + gje.getGuild().getName() + ". Initializing.. ");
    }
}