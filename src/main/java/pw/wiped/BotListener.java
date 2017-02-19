package pw.wiped;

import java.sql.SQLException;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;
import pw.wiped.util.Config;

class BotListener extends ListenerAdapter {

    private final SimpleLog LOG = SimpleLog.getLog("BotListener");

    @Override
    public void onMessageReceived (MessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith(Config.getCmdPrefix()) && !event.getAuthor().isBot()) {
            Bot.cmdMng.handleCommand(Bot.cmdMng.parse(event.getMessage().getContent(), event));
        }
    }



    @Override
    public void onReady (ReadyEvent event) {
        //Bot.log ("status", "Logged in as: " + event.getJDA().getSelfInfo().getUsername());
    }

    @Override
    public void onGuildJoin (GuildJoinEvent gje) {
        Config.initGuild(gje.getGuild());
        LOG.info("Joined guild " + gje.getGuild().getName() + ". Initializing.. ");
    }
}