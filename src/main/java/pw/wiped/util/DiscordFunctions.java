package pw.wiped.util;

import net.dv8tion.jda.core.entities.Game;
import pw.wiped.Bot;

/**
 * Created by wipeD on 03.04.2017.
 */
public class DiscordFunctions {

    public static void changeGamePlayed (String game) {
        Bot.getJDA().getPresence().setGame(Game.of(game));
    }

}
